package com.example.parking.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps exceptions to API error responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Handles business rule violations. */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(
        BusinessException exception
    ) {
        return ResponseEntity.badRequest()
            .body(build(exception.getMessage(), List.of()));
    }

    /** Handles missing resources. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
        ResourceNotFoundException exception
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(build(exception.getMessage(), List.of()));
    }

    /** Handles bean validation errors on request bodies. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
        MethodArgumentNotValidException exception
    ) {
        List<String> details = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .toList();

        return ResponseEntity.badRequest()
            .body(build("Request validation failed.", details));
    }

    /** Handles bean validation errors on query params. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraint(
        ConstraintViolationException exception
    ) {
        List<String> details = exception.getConstraintViolations()
            .stream()
            .map(violation -> violation.getMessage())
            .toList();

        return ResponseEntity.badRequest()
            .body(build("Request validation failed.", details));
    }

    /** Handles unexpected errors. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
        Exception exception
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(build("Unexpected server error.", List.of()));
    }

    /** Builds a standard error payload. */
    private ApiErrorResponse build(String message, List<String> details) {
        return new ApiErrorResponse(message, details, Instant.now());
    }
}
