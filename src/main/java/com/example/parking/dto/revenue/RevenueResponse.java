package com.example.parking.dto.revenue;

import java.math.BigDecimal;
import java.time.Instant;

/** Represents the revenue response payload. */
public record RevenueResponse(
    BigDecimal amount,
    String currency,
    Instant timestamp
) {
}
