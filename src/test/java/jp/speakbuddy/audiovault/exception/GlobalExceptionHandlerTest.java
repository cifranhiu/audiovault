package jp.speakbuddy.audiovault.exception;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("unused")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private Exception assertThrows;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundResponse() {
        ResourceNotFoundException exception = new ResourceNotFoundException("User not found");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleResourceNotFoundException(exception);
        var body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", body.get("message"));
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestResponse() {
        ValidationException exception = new ValidationException("Invalid input");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(exception);
        var body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", body.get("message"));
    }

    @Test
    void handleProcessFailureException_ShouldReturnInternalServerErrorResponse() {
        ProcessFailureException exception = new ProcessFailureException("Processing error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleProcessFailureException(exception);
        var body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Processing error", body.get("message"));
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerErrorResponse() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGlobalException(exception);
        var body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred.", body.get("message"));
    }
}
