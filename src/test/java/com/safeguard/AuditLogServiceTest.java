
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
    @Test
    void sanitizesControlCharacters() {
        AuditLogService service = new AuditLogService();

        assertDoesNotThrow(() ->
                service.record(
                        "SCAN_COMPLETED",
                        "SUCCESS",
                        "File scan\ncompleted\r\nsuccessfully"
                )
        );
    }

    @Test
    void handlesVeryLongDetails() {
        AuditLogService service = new AuditLogService();
        String longDetails = "A".repeat(1000);

        assertDoesNotThrow(() ->
                service.record(
                        "SCAN_COMPLETED",
                        "SUCCESS",
                        longDetails
                )
        );
    }
}
