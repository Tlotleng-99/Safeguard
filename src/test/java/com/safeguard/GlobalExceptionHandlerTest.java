
package com.safeguard;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void unexpectedErrorReturnsInternalServerError() {
        RuntimeException exception =
                new RuntimeException("Internal details");

        ResponseEntity<Map<String, Object>> response =
                handler.handleUnexpectedError(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "An unexpected error occurred. Please try again.",
                response.getBody().get("error")
        );
    }

    @Test
    void missingParameterReturnsBadRequest() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException(
                        "file", "MultipartFile"
                );

        var response = handler.handleMissingFile(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertEquals(
                "Please select a file to scan.",
                response.getBody().get("error")
        );
    }

    @Test
    void oversizedUploadReturnsBadRequest() {
        MaxUploadSizeExceededException exception =
                new MaxUploadSizeExceededException(10 * 1024 * 1024L);

        var response = handler.handleFileTooLarge(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertEquals(
                "File exceeds the 10 MB upload limit.",
                response.getBody().get("error")
        );
    }

}
