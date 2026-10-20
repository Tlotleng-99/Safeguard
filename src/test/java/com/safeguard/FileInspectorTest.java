
package com.safeguard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileInspectorTest {

    @TempDir
    Path tempDir;

    private final FileInspector inspector = new FileInspector();

    @Test
    void shouldInspectExistingFile() throws IOException {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "Hello Safeguard");

        FileInspector.InspectionResult result =
                inspector.inspect(file);

        assertTrue(result.exists());
        assertTrue(result.regularFile());
        assertTrue(result.readable());
        assertEquals(15, result.sizeBytes());
        assertEquals("File inspected successfully", result.message());
    }

    @Test
    void shouldReportMissingFile() {
        Path file = tempDir.resolve("missing.txt");

        FileInspector.InspectionResult result =
                inspector.inspect(file);

        assertFalse(result.exists());
        assertEquals("File does not exist", result.message());
    }

    @Test
    void shouldHandleNullPath() {
        FileInspector.InspectionResult result =
                inspector.inspect(null);

        assertFalse(result.exists());
        assertEquals("No file path supplied", result.message());
    }

    @Test
    void shouldRejectDirectory() throws IOException {
        Path directory = Files.createDirectory(
                tempDir.resolve("sample-folder")
        );

        FileInspector.InspectionResult result =
                inspector.inspect(directory);

        assertTrue(result.exists());
        assertFalse(result.regularFile());
        assertEquals("Path is not a regular file", result.message());
    }
}
