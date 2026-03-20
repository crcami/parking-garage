package com.example.parking.exception;

import java.time.Instant;
import java.util.List;

/** Represents a standard API error response. */
public record ApiErrorResponse(
    String message,
    List<String> details,
    Instant timestamp
) {
}
