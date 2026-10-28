
package com.safeguard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScanHistoryController.class)
class ScanHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScanHistoryService scanHistoryService;

    @Test
    void getScanHistoryShouldReturnOkAndEmptyList() throws Exception {
        when(scanHistoryService.latestScans()).thenReturn(List.of());

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(scanHistoryService).latestScans();
    }
}
