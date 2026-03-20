package com.example.parking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import org.springframework.stereotype.Service;

/** Calculates dynamic prices and parking charges. */
@Service
public class PricingPolicyService {

    private static final BigDecimal DISCOUNT_TEN = new BigDecimal("0.90");
    private static final BigDecimal NORMAL_PRICE = new BigDecimal("1.00");
    private static final BigDecimal PLUS_TEN = new BigDecimal("1.10");
    private static final BigDecimal PLUS_TWENTY_FIVE = new BigDecimal("1.25");

    /** Returns the multiplier for a given occupancy ratio. */
    public BigDecimal resolveMultiplier(
        long occupiedSpots,
        int maxCapacity
    ) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be positive.");
        }

        double occupancyRate = (double) occupiedSpots / maxCapacity;

        if (occupancyRate < 0.25d) {
            return DISCOUNT_TEN;
        }

        if (occupancyRate <= 0.50d) {
            return NORMAL_PRICE;
        }

        if (occupancyRate <= 0.75d) {
            return PLUS_TEN;
        }

        return PLUS_TWENTY_FIVE;
    }

    /** Freezes the hourly rate based on the multiplier. */
    public BigDecimal resolveHourlyRate(
        BigDecimal basePrice,
        BigDecimal multiplier
    ) {
        return basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /** Calculates the exit charge for a parking session. */
    public BigDecimal calculateCharge(
        BigDecimal hourlyRateSnapshot,
        Duration duration
    ) {
        long totalMinutes = duration.toMinutes();

        if (totalMinutes <= 30) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long billedHours = (long) Math.ceil(totalMinutes / 60.0d);

        return hourlyRateSnapshot.multiply(BigDecimal.valueOf(billedHours))
            .setScale(2, RoundingMode.HALF_UP);
    }
}
