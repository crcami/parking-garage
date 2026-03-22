package com.example.parking.dto.revenue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Represents the revenue query payload. */
public record RevenueRequest(
        @NotNull LocalDate date,
        @NotBlank String sector) {
}