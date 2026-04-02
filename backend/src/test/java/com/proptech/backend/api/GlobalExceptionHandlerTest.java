package com.proptech.backend.api;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void entityNotFound_returns404WithDetail() {
        ProblemDetail result = handler.handleEntityNotFound(
            new EntityNotFoundException("Property not found: abc-123")
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatus());
        assertEquals("Not Found", result.getTitle());
        assertEquals("Property not found: abc-123", result.getDetail());
        assertNull(result.getProperties());
    }

    @Test
    void illegalArgument_returns400WithDetail() {
        ProblemDetail result = handler.handleIllegalArgument(
            new IllegalArgumentException("El archivo supera el límite de 5MB")
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Bad Request", result.getTitle());
        assertEquals("El archivo supera el límite de 5MB", result.getDetail());
    }

    @Test
    void accessDenied_returns403() {
        ProblemDetail result = handler.handleAccessDenied(
            new AccessDeniedException("forbidden")
        );

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getStatus());
        assertEquals("Forbidden", result.getTitle());
    }

    @Test
    void genericException_returns500WithoutInternalDetails() {
        ProblemDetail result = handler.handleGeneric(
            new RuntimeException("NullPointerException: something internal broke")
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
        assertEquals("Internal Server Error", result.getTitle());
        assertEquals("An unexpected error occurred", result.getDetail());
        // El mensaje interno NO debe llegar al cliente
        assertNotEquals("NullPointerException: something internal broke", result.getDetail());
    }
}
