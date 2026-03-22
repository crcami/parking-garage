package com.example.parking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Calculates dynamic prices and parking charges. */
@Service
public class PricingPolicyService {

    private final BigDecimal factorDiscount;
    private final BigDecimal factorNormal;
    private final BigDecimal factorPlusStep1;
    private final BigDecimal factorPlusStep2;
    private final long toleranceMinutes;

    /** Creates the pricing policy service with externalized configuration. */
    public PricingPolicyService(
            @Value("${parking.pricing.factor.discount:0.90}") BigDecimal factorDiscount,
            @Value("${parking.pricing.factor.normal:1.00}") BigDecimal factorNormal,
            @Value("${parking.pricing.factor.plus-step1:1.10}") BigDecimal factorPlusStep1,
            @Value("${parking.pricing.factor.plus-step2:1.25}") BigDecimal factorPlusStep2,
            @Value("${parking.pricing.tolerance-minutes:30}") long toleranceMinutes) {
        this.factorDiscount = factorDiscount;
        this.factorNormal = factorNormal;
        this.factorPlusStep1 = factorPlusStep1;
        this.factorPlusStep2 = factorPlusStep2;
        this.toleranceMinutes = toleranceMinutes;
    }

    /** Returns the multiplier for a given occupancy ratio. */
    public BigDecimal resolveMultiplier(
            long occupiedSpots,
            int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be positive.");
        }

        double occupancyRate = (double) occupiedSpots / maxCapacity;

        if (occupancyRate < 0.25d) {
            return factorDiscount;
        }

        if (occupancyRate <= 0.50d) {
            return factorNormal;
        }

        if (occupancyRate <= 0.75d) {
            return factorPlusStep1;
        }

        return factorPlusStep2;
    }

    /** Freezes the hourly rate based on the multiplier. */
    public BigDecimal resolveHourlyRate(
            BigDecimal basePrice,
            BigDecimal multiplier) {
        return basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /** Calculates the exit charge for a parking session. */
    public BigDecimal calculateCharge(
            BigDecimal hourlyRateSnapshot,
            Duration duration) {
        long totalMinutes = duration.toMinutes();

        if (totalMinutes <= toleranceMinutes) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long billedHours = (long) Math.ceil(totalMinutes / 60.0d);

        return hourlyRateSnapshot.multiply(BigDecimal.valueOf(billedHours))
                .setScale(2, RoundingMode.HALF_UP);
    }
}