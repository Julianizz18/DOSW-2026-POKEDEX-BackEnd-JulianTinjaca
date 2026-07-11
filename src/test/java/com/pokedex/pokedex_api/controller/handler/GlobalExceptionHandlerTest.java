package com.pokedex.pokedex_api.controller.handler;

import com.pokedex.pokedex_api.controller.dto.response.ApiError;
import com.pokedex.pokedex_api.core.exception.BusinessException;
import com.pokedex.pokedex_api.core.exception.DuplicateResourceException;
import com.pokedex.pokedex_api.core.exception.InvalidOperationException;
import com.pokedex.pokedex_api.core.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/pokemon/1");
    }

    @Test
    void handleNotFound_returns404() {
        ResponseEntity<ApiError> response = handler.handleNotFound(
                new ResourceNotFoundException("Pokemon", "id", 1L), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().errorCode()).isEqualTo("NOT_FOUND");
    }

    @Test
    void handleDuplicate_returns409() {
        ResponseEntity<ApiError> response = handler.handleDuplicate(
                new DuplicateResourceException("Pokemon", "nationalNumber", 25), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleInvalidOperation_returns400() {
        ResponseEntity<ApiError> response = handler.handleInvalidOperation(
                new InvalidOperationException("Equipo lleno"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleBusiness_returns400() {
        ResponseEntity<ApiError> response = handler.handleBusiness(
                new BusinessException("Error generico", "GENERIC"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errorCode()).isEqualTo("GENERIC");
    }

    @Test
    void handleValidation_mapsFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(
                List.of(new FieldError("registerRequest", "email", "debe ser un email valido")));

        ResponseEntity<ApiError> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().fieldErrors()).hasSize(1);
        assertThat(response.getBody().fieldErrors().get(0).field()).isEqualTo("email");
    }

    @Test
    void handleBadCredentials_returns401() {
        ResponseEntity<ApiError> response = handler.handleBadCredentials(
                new BadCredentialsException("bad"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().errorCode()).isEqualTo("INVALID_CREDENTIALS");
    }

    @Test
    void handleAccessDenied_returns403() {
        ResponseEntity<ApiError> response = handler.handleAccessDenied(
                new AccessDeniedException("denied"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void handleUnexpected_returns500() {
        ResponseEntity<ApiError> response = handler.handleUnexpected(new RuntimeException("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().errorCode()).isEqualTo("INTERNAL_ERROR");
    }
}
