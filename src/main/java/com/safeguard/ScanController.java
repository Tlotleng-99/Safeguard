
package com.safeguard;

import org.springframework.http.HttpStatus;
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

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final MalwareSignatureChecker signatureChecker =
            new MalwareSignatureChecker();

    @PostMapping(
            value = "/scan",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> scanFile(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Please select a non-empty file."
            ));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "File exceeds the 10 MB upload limit."
            ));
        }

        Path temporaryFile = null;

        try {
            temporaryFile = Files.createTempFile(
                    "safeguard-upload-", ".tmp");

            file.transferTo(temporaryFile);

            FileInspector inspector = new FileInspector();
            var inspection = inspector.inspect(temporaryFile);
            var result = signatureChecker.scan(temporaryFile);

            return ResponseEntity.ok(Map.of(
                    "fileName", file.getOriginalFilename() == null
                            ? "uploaded-file"
                            : Path.of(file.getOriginalFilename())
                            .getFileName().toString(),
                    "sizeBytes", file.getSize(),
                    "sha256", result.sha256(),
                    "signatureMatched", result.signatureMatched(),
                    "message", result.message(),
                    "inspection", inspection.toString()
            ));

        } catch (IOException | RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "The file could not be scanned."
                    ));
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // Temporary-file cleanup is best effort.
                }
            }
        }
    }
}
