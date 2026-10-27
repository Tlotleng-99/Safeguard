
package com.safeguard;

import java.time.Instant;

public record AuditEvent(
        Instant timestamp,
        String eventType,
        String outcome,
        String details
) {
}
