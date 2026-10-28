
package com.safeguard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditLogService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuditLogService.class);

    public void record(
            String eventType,
            String outcome,
            String details
    ) {
        AuditEvent event = new AuditEvent(
                Instant.now(),
                eventType,
                outcome,
                sanitize(details)
        );

        logger.info(
                "AUDIT timestamp={} eventType={} outcome={} details={}",
                event.timestamp(),
                event.eventType(),
                event.outcome(),
                event.details()
        );
    }

    private String sanitize(String details) {
        if (details == null) {
            return "";
        }

        String sanitized = details.replaceAll("[\\p{Cntrl}]", " ");

        if (sanitized.length() > 500) {
            sanitized = sanitized.substring(0, 500);
        }

        return sanitized;
    }
}
