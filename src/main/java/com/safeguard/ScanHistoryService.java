
package com.safeguard;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ScanHistoryService {

    private final ScanHistoryRepository repository;

    public ScanHistoryService(ScanHistoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ScanHistory save(
            String fileName,
            long sizeBytes,
            String sha256,
            String outcome,
            String message
    ) {
        ScanHistory record = new ScanHistory(
                Instant.now(),
                safeText(fileName, 255),
                sizeBytes,
                safeText(sha256, 64),
                safeText(outcome, 30),
                safeText(message, 500)
        );

        return repository.save(record);
    }

    @Transactional(readOnly = true)
    public List<ScanHistory> latestScans() {
        return repository.findAll(
                PageRequest.of(0, 100,
                        org.springframework.data.domain.Sort
                                .by(org.springframework.data.domain.Sort.Direction.DESC,
                                        "scannedAt"))
        ).getContent();
    }

    private String safeText(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        String cleaned = value
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ');

        return cleaned.substring(0, Math.min(cleaned.length(), maxLength));
    }
}
