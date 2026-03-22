package com.example.parking.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Maps exceptions to API error responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            GlobalExceptionHandler.class);

    /** Handles business rule violations. */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(
            BusinessException exception) {
        LOGGER.warn("BusinessException: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(build(exception.getMessage(), List.of()));
    }

    /** Handles missing resources. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(build(exception.getMessage(), List.of()));
    }

    /** Handles malformed JSON payloads. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedBody(
            HttpMessageNotReadableException exception) {
        LOGGER.warn("Malformed body: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(build("Malformed request body.", List.of()));
    }

    /** Handles type mismatch on query/path parameters (e.g. wrong date format). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {
        String detail = String.format(
                "Invalid value for parameter '%s': expected format is yyyy-MM-dd.",
                exception.getName());

        return ResponseEntity.badRequest()
                .body(build("Request parameter type mismatch.", List.of(detail)));
    }

    /** Handles bean validation errors on request bodies. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception) {
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
            ConstraintViolationException exception) {
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
            Exception exception) {
        LOGGER.error("Unexpected server error.", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        build(
                                "Unexpected server error.",
                                List.of(exception.getClass().getSimpleName())));
    }

    /** Builds a standard error payload. */
    private ApiErrorResponse build(String message, List<String> details) {
        return new ApiErrorResponse(message, details, Instant.now());
    }
}