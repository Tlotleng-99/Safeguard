
package com.safeguard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileHasherTest {

    @TempDir
    Path tempDir;

    private final FileHasher hasher = new FileHasher();

    @Test
    void shouldCalculateKnownSHA256Hash() throws IOException {
        Path file = tempDir.resolve("hello.txt");
        Files.writeString(file, "hello");

        String hash = hasher.calculateSHA256(file);

        assertEquals(
                "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
                hash
        );
    }

    @Test
    void shouldReturnSameHashForSameContent() throws IOException {
        Path first = tempDir.resolve("first.txt");
        Path second = tempDir.resolve("second.txt");

        Files.writeString(first, "Safeguard");
        Files.writeString(second, "Safeguard");

        assertEquals(
                hasher.calculateSHA256(first),
                hasher.calculateSHA256(second)
        );
    }

    @Test
    void shouldReturnDifferentHashesForDifferentContent()
            throws IOException {
        Path first = tempDir.resolve("first.txt");
        Path second = tempDir.resolve("second.txt");

        Files.writeString(first, "hello");
        Files.writeString(second, "goodbye");

        assertNotEquals(
                hasher.calculateSHA256(first),
                hasher.calculateSHA256(second)
        );
    }

    @Test
    void shouldRejectNullPath() {
        assertThrows(
                IllegalArgumentException.class,
                () -> hasher.calculateSHA256(null)
        );
    }

    @Test
    void shouldRejectMissingFile() {
        Path missing = tempDir.resolve("missing.txt");

        assertThrows(
                IOException.class,
                () -> hasher.calculateSHA256(missing)
        );
    }
}
