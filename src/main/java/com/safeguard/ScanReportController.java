
package com.safeguard;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;
import java.util.List;
@RestController
@RequestMapping("/api/reports")
public class ScanReportController {

    private final ScanHistoryService scanHistoryService;

    public ScanReportController(ScanHistoryService scanHistoryService) {
        this.scanHistoryService = scanHistoryService;
    }
    @GetMapping(value = "/scans.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportScansCsv() {
        List<ScanHistory> scans = scanHistoryService.latestScans();

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Scanned At,File Name,Size Bytes,SHA-256,Outcome,Message\n");

        for (ScanHistory scan : scans) {
            csv.append(scan.getId()).append(',')
                    .append(csvCell(scan.getScannedAt().toString())).append(',')
                    .append(csvCell(scan.getFileName())).append(',')
                    .append(scan.getSizeBytes()).append(',')
                    .append(csvCell(scan.getSha256())).append(',')
                    .append(csvCell(scan.getOutcome())).append(',')
                    .append(csvCell(scan.getMessage())).append('\n');
        }
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"safeguard-scan-report.csv\""
                )
                .contentType(new MediaType(
                        "text", "csv", StandardCharsets.UTF_8
                ))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
    private String csvCell(String value) {
        if (value == null) {
            return "\"\"";
        }
        // Prevent spreadsheet formula injection.
        String safe = value.replaceAll("^[\\s\\u0000-\\u001F]*[=+@-].*", "'$0");

        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }


}
