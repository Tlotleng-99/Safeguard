
package com.safeguard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogServiceTest {

    @Test
    void recordsAnAuditEventWithoutThrowing() {
        AuditLogService service = new AuditLogService();

        assertDoesNotThrow(() ->
                service.record(
                        "SCAN_COMPLETED",
                        "SUCCESS",
                        "File scan completed"
                )
        );
    }
    @Test
    void handlesNullDetails() {
        AuditLogService service = new AuditLogService();

        assertDoesNotThrow(() ->
                service.record(
                        "SCAN_REJECTED",
                        "REJECTED",
                        null
                )
        );
    }
}
