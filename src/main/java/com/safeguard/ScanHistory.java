
package com.safeguard;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(
        name = "scan_history",
        indexes = @Index(
                name = "idx_scan_history_timestamp",
                columnList = "scanned_at"
        )
)
public class ScanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant scannedAt;
    private String fileName;
    private long sizeBytes;
    private String sha256;
    private String outcome;
    private String message;

    protected ScanHistory() {
        // Required by JPA.
    }

    public ScanHistory(
            Instant scannedAt,
            String fileName,
            long sizeBytes,
            String sha256,
            String outcome,
            String message
    ) {
        this.scannedAt = scannedAt;
        this.fileName = fileName;
        this.sizeBytes = sizeBytes;
        this.sha256 = sha256;
        this.outcome = outcome;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Instant getScannedAt() {
        return scannedAt;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getSha256() {
        return sha256;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getMessage() {
        return message;
    }
}
