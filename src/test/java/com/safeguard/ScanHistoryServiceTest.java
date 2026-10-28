
package com.safeguard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import static org.mockito.Mockito.verify;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScanHistoryServiceTest {

    private ScanHistoryRepository repository;
    private ScanHistoryService service;

    @BeforeEach
    void setUp() {
        repository = mock(ScanHistoryRepository.class);
        service = new ScanHistoryService(repository);
    }

    @Test
    void saveShouldStoreScanRecord() {
        when(repository.save(any(ScanHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScanHistory saved = service.save(
                "sample.txt",
                128,
                "a".repeat(64),
                "NO_MATCH",
                "No known signature matched"
        );

        assertNotNull(saved);
        assertEquals("sample.txt", saved.getFileName());
        assertEquals(128, saved.getSizeBytes());
        assertEquals("a".repeat(64), saved.getSha256());
        assertEquals("NO_MATCH", saved.getOutcome());
        assertEquals("No known signature matched", saved.getMessage());
        assertNotNull(saved.getScannedAt());

        verify(repository).save(any(ScanHistory.class));
    }

    @Test
    void saveShouldSanitizeControlCharacters() {
        when(repository.save(any(ScanHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScanHistory saved = service.save(
                "sample\nfile.txt",
                10,
                "abc",
                "NO_MATCH",
                "line one\r\nline two\tend"
        );

        assertEquals("sample file.txt", saved.getFileName());
        assertEquals("line one  line two end", saved.getMessage());
    }

    @Test
    void saveShouldLimitTextLengths() {
        when(repository.save(any(ScanHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScanHistory saved = service.save(
                "f".repeat(300),
                10,
                "a".repeat(100),
                "N".repeat(40),
                "m".repeat(600)
        );

        assertEquals(255, saved.getFileName().length());
        assertEquals(64, saved.getSha256().length());
        assertEquals(30, saved.getOutcome().length());
        assertEquals(500, saved.getMessage().length());
    }

    @Test
    void saveShouldHandleNullTextValues() {
        when(repository.save(any(ScanHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScanHistory saved = service.save(
                null, 0, null, null, null
        );

        assertEquals("", saved.getFileName());
        assertEquals("", saved.getSha256());
        assertEquals("", saved.getOutcome());
        assertEquals("", saved.getMessage());
    }

    @Test
    void latestScansShouldRequestNewest100Records() {
        when(repository.findAll(
                org.mockito.ArgumentMatchers.<Pageable>any()
        )).thenReturn(new PageImpl<>(List.of()));

        List<ScanHistory> scans = service.latestScans();

        assertNotNull(scans);
        assertTrue(scans.isEmpty());

        verify(repository).findAll(
                org.mockito.ArgumentMatchers.<Pageable>argThat(pageable ->
                        pageable.getPageNumber() == 0
                                && pageable.getPageSize() == 100
                                && pageable.getSort()
                                .getOrderFor("scannedAt") != null
                                && pageable.getSort()
                                .getOrderFor("scannedAt")
                                .isDescending()
                )
        );
    }
}
