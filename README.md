# Safeguard

Safeguard is a Java cybersecurity learning project focused on file inspection, SHA-256 hashing, and basic signature-based malware detection.

## Current features

- File existence and readability checks
- File size inspection
- Error handling
- SHA-256 file hashing
- Local signature database integration
- Hash-based signature matching
- Signature format validation and duplicate detection
- Automated unit tests using JUnit 5

## Project structure

```text
Safeguard/
├── pom.xml
├── README.md
├── sample.txt
└── src/
    ├── main/
    │   ├── java/com/safeguard/
    │   │   ├── SafeguardApp.java
    │   │   ├── FileInspector.java
    │   │   ├── FileHasher.java
    │   │   ├── MalwareSignatureChecker.java
    │   │   └── SignatureDatabase.java
    │   └── resources/
    │       └── signatures.txt
    └── test/
        ├── java/com/safeguard/
        │   ├── FileInspectorTest.java
        │   ├── FileHasherTest.java
        │   ├── MalwareSignatureCheckerTest.java
        │   └── SignatureDatabaseTest.java
        └── resources/
            ├── valid-signatures.txt
            └── invalid-signatures.txt
```

## Requirements

- Java JDK 21 or later
- Apache Maven

## Run the tests

Open a terminal in the project root and execute:

```bash
mvn clean test
```

## Build the project

```bash
mvn clean package
```

## Run Safeguard

After compiling, run the application with a file path:

```bash
java -cp target/classes com.safeguard.SafeguardApp sample.txt
```

Replace `sample.txt` with the path of the file you want to inspect.

## Signature database

The `signatures.txt` resource stores signatures in this format:

```text
SHA256_HASH=SIGNATURE_NAME
```

Each hash must contain 64 hexadecimal characters. Blank lines and lines beginning with `#` are ignored.

## Limitations

Safeguard is an educational prototype, not a complete antivirus application. Detection depends on the signatures in the local database. 
A file with no matching signature is not necessarily safe. Test signatures are for demonstration only and must not be treated as real malware intelligence.


WTC-ETZVEYCV


