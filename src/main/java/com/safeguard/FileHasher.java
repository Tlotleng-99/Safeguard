
package com.safeguard;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class FileHasher {

    public String calculateSHA256(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        if (!Files.isRegularFile(path)) {
            throw new IOException("Path is not a regular file");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (InputStream input = Files.newInputStream(path)) {
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            return HexFormat.of().formatHex(digest.digest());

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
