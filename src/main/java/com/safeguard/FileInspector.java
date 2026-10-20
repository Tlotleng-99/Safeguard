
package com.safeguard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInspector {

    public InspectionResult inspect(Path path) {
        if (path == null) {
            return new InspectionResult(
                    false, false, false, 0,
                    "No file path supplied"
            );
        }

        if (!Files.exists(path)) {
            return new InspectionResult(
                    false, false, false, 0,
                    "File does not exist"
            );
        }

        if (!Files.isRegularFile(path)) {
            return new InspectionResult(
                    true, false, false, 0,
                    "Path is not a regular file"
            );
        }

        if (!Files.isReadable(path)) {
            return new InspectionResult(
                    true, true, false, 0,
                    "File is not readable"
            );
        }

        try {
            long size = Files.size(path);

            return new InspectionResult(
                    true, true, true, size,
                    "File inspected successfully"
            );
        } catch (IOException e) {
            return new InspectionResult(
                    true, true, false, 0,
                    "Could not read file metadata"
            );
        }
    }

    public record InspectionResult(
            boolean exists,
            boolean regularFile,
            boolean readable,
            long sizeBytes,
            String message
    ) {}
}
