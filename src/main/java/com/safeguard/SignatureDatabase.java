
package com.safeguard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SignatureDatabase {

    public Map<String, String> load(String resourceName)
            throws IOException {

        if (resourceName == null || resourceName.isBlank()) {
            throw new IllegalArgumentException(
                    "Resource name cannot be blank");
        }

        InputStream stream =
                getClass().getClassLoader()
                        .getResourceAsStream(resourceName);

        if (stream == null) {
            throw new IOException(
                    "Signature resource not found: " + resourceName);
        }

        Map<String, String> signatures = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        stream, StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int separator = line.indexOf('=');

                if (separator <= 0 || separator == line.length() - 1) {
                    throw new IOException(
                            "Invalid signature entry at line "
                                    + lineNumber);
                }

                String hash = line.substring(0, separator)
                        .trim().toLowerCase();
                String name = line.substring(separator + 1).trim();

                if (!hash.matches("[0-9a-f]{64}") || name.isBlank()) {
                    throw new IOException(
                            "Invalid signature entry at line "
                                    + lineNumber);
                }

                if (signatures.putIfAbsent(hash, name) != null) {
                    throw new IOException(
                            "Duplicate signature hash at line "
                                    + lineNumber);
                }
            }
        }

        return Map.copyOf(signatures);
    }
}
