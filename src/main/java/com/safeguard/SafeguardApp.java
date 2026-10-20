
package com.safeguard;

import java.io.IOException;
import java.nio.file.Path;

public class SafeguardApp {

    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println("       SAFEGUARD v1.0");
        System.out.println("    Cybersecurity File Checker");
        System.out.println("==================================");

        if (args.length == 0) {
            System.out.println("Usage: SafeguardApp <file-path>");
            System.out.println("Example: SafeguardApp sample.txt");
            return;
        }

        Path path;

        try {
            path = Path.of(args[0]);
        } catch (RuntimeException e) {
            System.out.println("Invalid file path.");
            return;
        }

        FileInspector inspector = new FileInspector();
        FileInspector.InspectionResult inspection =
                inspector.inspect(path);

        System.out.println("File: " + path.toAbsolutePath());
        System.out.println("Exists: " + inspection.exists());
        System.out.println("Regular file: " + inspection.regularFile());
        System.out.println("Readable: " + inspection.readable());
        System.out.println("Size: " + inspection.sizeBytes() + " bytes");
        System.out.println("Status: " + inspection.message());

        if (!inspection.exists()
                || !inspection.regularFile()
                || !inspection.readable()) {
            System.out.println("Scan stopped: file cannot be inspected.");
            return;
        }

        try {
            MalwareSignatureChecker checker =
                    new MalwareSignatureChecker();

            MalwareSignatureChecker.ScanResult result =
                    checker.scan(path);

            System.out.println("----------------------------------");
            System.out.println("SHA-256: " + result.sha256());
            System.out.println("Signature match: "
                    + result.signatureMatched());
            System.out.println("Scan result: " + result.message());

            if (result.signatureMatched()) {
                System.out.println(
                        "WARNING: A known test signature matched.");
            } else {
                System.out.println(
                        "No known signature matched. This does not prove "
                                + "the file is safe.");
            }

        } catch (IOException e) {
            System.out.println("Could not scan file: " + e.getMessage());
        }
    }
}
