package com.safeguard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScanReportController.class)
class ScanReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScanHistoryService scanHistoryService;

    @Test
    void exportScansCsvShouldReturnCsvHeadersAndDownloadFilename()
            throws Exception {

        when(scanHistoryService.latestScans()).thenReturn(List.of());

        mockMvc.perform(get("/api/reports/scans.csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"safeguard-scan-report.csv\""
                ))
                .andExpect(content().string(
                        "ID,Scanned At,File Name,Size Bytes,SHA-256,Outcome,Message\n"
                ));

        verify(scanHistoryService).latestScans();
    }

    @Test
    void exportScansCsvShouldEscapeTextAndProtectAgainstFormulaInjection()
            throws Exception {

        ScanHistory scan = new ScanHistory(
                Instant.parse("2026-01-01T10:00:00Z"),
                "=1+1",
                128,
                "abc123",
                "NO_MATCH",
                "Message with, comma and \"quotes\""
        );

        when(scanHistoryService.latestScans())
                .thenReturn(List.of(scan));

        mockMvc.perform(get("/api/reports/scans.csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("\"'=1+1\"")
                ))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "\"Message with, comma and \"\"quotes\"\"\""
                        )
                ));

        verify(scanHistoryService).latestScans();
    }

}
