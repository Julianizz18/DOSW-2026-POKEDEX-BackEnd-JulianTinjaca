package com.pokedex.pokedex_api.controller.handler;

import com.pokedex.pokedex_api.controller.dto.response.ApiError;
import com.pokedex.pokedex_api.core.exception.BusinessException;
import com.pokedex.pokedex_api.core.exception.DuplicateResourceException;
import com.pokedex.pokedex_api.core.exception.InvalidOperationException;
import com.pokedex.pokedex_api.core.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return status(HttpStatus.NOT_FOUND)
                .body(buildError(404, ex.getErrorCode(), ex.getMessage(), req.getRequestURI(), List.of()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        return status(HttpStatus.CONFLICT)
                .body(buildError(409, ex.getErrorCode(), ex.getMessage(), req.getRequestURI(), List.of()));
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiError> handleInvalidOperation(InvalidOperationException ex, HttpServletRequest req) {
        return status(HttpStatus.BAD_REQUEST)
                .body(buildError(400, ex.getErrorCode(), ex.getMessage(), req.getRequestURI(), List.of()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return status(HttpStatus.BAD_REQUEST)
                .body(buildError(400, ex.getErrorCode(), ex.getMessage(), req.getRequestURI(), List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ApiError.FieldError(e.getField(), e.getDefaultMessage()))
                .toList();
        return status(HttpStatus.BAD_REQUEST)
                .body(buildError(400, "VALIDATION_ERROR", "Error de validación en los datos de entrada",
                        req.getRequestURI(), errors));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return status(HttpStatus.UNAUTHORIZED)
                .body(buildError(401, "INVALID_CREDENTIALS", "Correo o contraseña incorrectos",
                        req.getRequestURI(), List.of()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return status(HttpStatus.FORBIDDEN)
                .body(buildError(403, "ACCESS_DENIED", "No tienes permisos para esta acción",
                        req.getRequestURI(), List.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Error no controlado", ex);
        return status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(500, "INTERNAL_ERROR", "Ocurrió un error inesperado",
                        req.getRequestURI(), List.of()));
    }

    private ApiError buildError(int status, String code, String msg, String path, List<ApiError.FieldError> errors) {
        return new ApiError(status, code, msg, path, LocalDateTime.now(), errors);
    }
}
