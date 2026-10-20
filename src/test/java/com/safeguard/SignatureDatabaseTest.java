package com.safeguard;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SignatureDatabaseTest {

    private final SignatureDatabase database = new SignatureDatabase();

    @Test
    void shouldLoadValidSignatures() throws IOException {
        Map<String, String> signatures =
                database.load("valid-signatures.txt");

        assertEquals(
                "DEMO",
                signatures.get(
                        "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824")
        );

        assertEquals(1, signatures.size());
    }

    @Test
    void shouldRejectInvalidSignatureHash() {
        assertThrows(
                IOException.class,
                () -> database.load("invalid-signatures.txt")
        );
    }

    @Test
    void shouldRejectBlankResourceName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> database.load(" ")
        );
    }

    @Test
    void shouldReportMissingResource() {
        assertThrows(
                IOException.class,
                () -> database.load("missing-signatures.txt")
        );
    }
}