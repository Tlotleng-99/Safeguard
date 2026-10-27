
package com.safeguard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class ScanController {
    private static final Logger logger =
            LoggerFactory.getLogger(ScanController.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final MalwareSignatureChecker signatureChecker =
            new MalwareSignatureChecker();
    private final AuditLogService auditLogService;
    private final ScanHistoryService scanHistoryService;

    public ScanController(
            AuditLogService auditLogService,
            ScanHistoryService scanHistoryService) {
        this.auditLogService = auditLogService;
        this.scanHistoryService = scanHistoryService;
    }
    @PostMapping(
            value = "/scan",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> scanFile(
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            auditLogService.record(
                    "SCAN_REJECTED",
                    "REJECTED",
                    "Empty file upload"
            );

            scanHistoryService.save(
                    safeFilename,
                    file.getSize(),
                    result.sha256(),
                    result.signatureMatched() ? "MATCHED" : "NO_MATCH",
                    result.message()
            );

            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Please select a non-empty file."
            ));
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            auditLogService.record(
                    "SCAN_REJECTED",
                    "REJECTED",
                    "Upload exceeded the 10 MB limit"
            );
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "File exceeds the 10 MB upload limit."
            ));
        }
        Path temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile(
                    "safeguard-upload-", ".tmp"
            );
            file.transferTo(temporaryFile);
            FileInspector inspector = new FileInspector();
            var inspection = inspector.inspect(temporaryFile);
            var result = signatureChecker.scan(temporaryFile);

            String originalFilename = file.getOriginalFilename();
            String safeFilename = "uploaded-file";

            if (originalFilename != null && !originalFilename.isBlank()) {
                try {
                    String filename = originalFilename.replace('\\', '/');
                    safeFilename = Path.of(filename).getFileName().toString();

                    if (safeFilename.isBlank()
                            || safeFilename.equals(".")
                            || safeFilename.equals("..")) {
                        safeFilename = "uploaded-file";
                    }
                } catch (RuntimeException exception) {
                    safeFilename = "uploaded-file";
                }
            }
            auditLogService.record(
                    "SCAN_COMPLETED",
                    result.signatureMatched() ? "MATCHED" : "NO_MATCH",
                    "File scan completed; sizeBytes=" + file.getSize()
            );

            return ResponseEntity.ok(Map.of(
                    "fileName", safeFilename,
                    "sizeBytes", file.getSize(),
                    "sha256", result.sha256(),
                    "signatureMatched", result.signatureMatched(),
                    "message", result.message(),
                    "inspection", inspection.toString()
            ));

        } catch (IOException | RuntimeException exception) {
            auditLogService.record(
                    "SCAN_ERROR",
                    "ERROR",
                    "File scan failed"
            );
            logger.error("File scan failed.", exception);
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "The file could not be scanned."
            ));
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException exception) {
                    logger.warn("Could not delete temporary upload file.");
                }
            }
        }
    }
}
