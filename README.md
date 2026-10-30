# Safeguard

Safeguard is a Java cybersecurity learning project focused on file inspection, SHA-256 hashing, and basic hash-signature matching. It provides both a command-line interface and a Spring Boot web interface for scanning files.

**Important:** Safeguard is an educational prototype, not a complete antivirus application.

## Features

- File existence, readability, and size inspection
- SHA-256 file hashing
- Local signature database integration
- Hash-based signature matching
- Signature format validation and duplicate detection
- Web interface for uploading and scanning files
- REST API for file scanning
- File upload size limits and empty-file validation
- Temporary-file cleanup and safe error responses
- Automated tests using JUnit 5 and Spring Boot testing support
- Persistent scan history using an H2 database
- REST API for retrieving recent scan history
- CSV export of scan reports
- Audit logging for scan events

## Requirements

- Java JDK 21 or later
- Apache Maven
- A web browser for the web interface

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
    │   │   ├── SignatureDatabase.java
    │   │   ├── SafeguardWebApplication.java
    │   │   ├── ScanController.java
    │   │   └── GlobalExceptionHandler.java
    │   └── resources/
    │       ├── application.properties
    │       ├── signatures.txt
    │       └── static/
    │           ├── index.html
    │           ├── style.css
    │           └── app.js
    └── test/
        ├── java/com/safeguard/
        │   ├── FileInspectorTest.java
        │   ├── FileHasherTest.java
        │   ├── MalwareSignatureCheckerTest.java
        │   ├── SignatureDatabaseTest.java
        │   ├── ScanControllerTest.java
        │   └── GlobalExceptionHandlerTest.java
        └── resources/
            ├── valid-signatures.txt
            ├── test-signatures.txt
            └── invalid-signatures.txt
```

## Run the automated tests

From the project root, run:

```bash
mvn clean test
```

A successful run should finish with `BUILD SUCCESS`.

## Build the project

```bash
mvn clean package
```

This compiles the project, runs the configured tests, and packages the application.

## Run the command-line scanner

After building the project, inspect a file using:

```bash
java -cp target/classes com.safeguard.SafeguardApp sample.txt
```

Replace `sample.txt` with the path of the file you want to inspect.

## Run the web application

Start the Spring Boot application using your IDE, or use Maven:

```bash
mvn spring-boot:run
```

Open the following address in your browser:

```text
http://localhost:8080/
```

The web interface allows you to select a file and submit it for scanning.

## File-scanning API

Safeguard exposes the following endpoint:

| Property | Value |
|---|---|
| Method | `POST` |
| Endpoint | `/api/scan` |
| Content type | `multipart/form-data` |
| File field | `file` |
| Maximum file size | 10 MB |

### Example request

With Safeguard running, use:

```bash
curl -i -X POST http://localhost:8080/api/scan \
  -F "file=@sample.txt"
```
````bash opening the browser
xdg-open http://localhost:8080
The API returns JSON containing information such as the filename, file size, SHA-256 hash, signature-match status, scan message, and file-inspection result.
````
### Error handling

The API validates uploads and returns safe error messages for supported error cases.

Examples include:

- Empty file: HTTP 400
- File exceeding the upload limit: HTTP 400
- Missing file: HTTP 400
- Unsupported request content type: HTTP 415
- Unexpected server error: HTTP 500

## Signature database

The `signatures.txt` resource stores signatures in this format:

```text
SHA256_HASH=SIGNATURE_NAME
```

Each SHA-256 hash must contain 64 hexadecimal characters. Blank lines and lines beginning with `#` are ignored. Invalid entries and duplicate signatures are handled by the signature database validation logic.

## Security considerations and limitations

Safeguard is an educational prototype and should not be used as a replacement for established antivirus or endpoint security software.

- Detection depends on the signatures available in the local database.
- A file with no matching signature is not necessarily safe.
- SHA-256 hashing identifies file content; it does not by itself determine whether a file is malicious.
- Demonstration and test signatures are not real malware intelligence.
- The application should be tested in a controlled environment and should not be exposed publicly without additional security review.

## Future improvements

Possible future enhancements include:

- Integrating a trusted malware intelligence source
- Adding additional file-analysis methods
- Improving security monitoring and audit logging
- Adding deployment documentation and automated CI testing
- here is my youtube link also
  https://youtu.be/YHitCOc1KTM?si=nMrb2kyXhNZL3iIf

WTC-ETZVEYCV


