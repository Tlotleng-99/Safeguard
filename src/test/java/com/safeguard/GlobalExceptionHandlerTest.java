
package com.safeguard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerTest {

    @ExceptionHandler({
            MissingServletRequestPartException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<Map<String, Object>> handleMissingFile(
            Exception exception) {

        return ResponseEntity.badRequest().body(Map.of(
                "error", "Please select a file to scan."
        ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleFileTooLarge(
            MaxUploadSizeExceededException exception) {

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of(
                        "error", "File exceeds the 10 MB upload limit."
                ));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException exception) {

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(Map.of(
                        "error", "Unsupported request content type."
                ));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> handleMultipartError(
            MultipartException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof MaxUploadSizeExceededException
                    || (cause.getMessage() != null
                    && cause.getMessage().toLowerCase()
                    .contains("maximum upload size exceeded"))) {

                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(Map.of(
                                "error",
                                "File exceeds the 10 MB upload limit."
                        ));
            }
            cause = cause.getCause();
        }
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Please select a file to scan."
        ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedError(
            Exception exception) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error",
                        "An unexpected error occurred. Please try again."
                ));
    }
}
