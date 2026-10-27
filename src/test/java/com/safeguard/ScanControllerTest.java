
package com.safeguard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadTextFileReturnsScanResults() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Safeguard API test file".getBytes()
        );

        mockMvc.perform(multipart("/api/scan").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("sample.txt"))
                .andExpect(jsonPath("$.sizeBytes").value(23))
                .andExpect(jsonPath("$.sha256").exists())
                .andExpect(jsonPath("$.signatureMatched").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void emptyFileReturnsBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/scan").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Please select a non-empty file."));
    }


    @Test
    void oversizedFileReturnsBadRequest() throws Exception {
        byte[] contents = new byte[10 * 1024 * 1024 + 1];

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.txt",
                MediaType.TEXT_PLAIN_VALUE,
                contents
        );

        mockMvc.perform(multipart("/api/scan").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("File exceeds the 10 MB upload limit."));
    }


    @Test
    void missingFileReturnsBadRequest() throws Exception {
        mockMvc.perform(multipart("/api/scan"))
                .andExpect(status().isBadRequest());
    }


}
