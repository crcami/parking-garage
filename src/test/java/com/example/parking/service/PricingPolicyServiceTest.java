package com.example.parking.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.Test;

/** Tests pricing policy rules. */
class PricingPolicyServiceTest {

    private final PricingPolicyService service = new PricingPolicyService(
            new BigDecimal("0.90"),
            new BigDecimal("1.00"),
            new BigDecimal("1.10"),
            new BigDecimal("1.25"),
            30L);

    /** Verifies that short stays are free. */
    @Test
    void shouldReturnZeroForThirtyMinutesOrLess() {
        BigDecimal result = service.calculateCharge(
                new BigDecimal("10.00"),
                Duration.ofMinutes(30));

        assertThat(result).isEqualByComparingTo("0.00");
    }

    /** Verifies that long stays are rounded up per hour. */
    @Test
    void shouldRoundUpHoursAfterFreeWindow() {
        BigDecimal result = service.calculateCharge(
                new BigDecimal("10.00"),
                Duration.ofMinutes(61));

        assertThat(result).isEqualByComparingTo("20.00");
    }

    /** Verifies the dynamic pricing multiplier thresholds. */
    @Test
    void shouldResolveExpectedMultiplier() {
        assertThat(service.resolveMultiplier(10, 100))
                .isEqualByComparingTo("0.90");
        assertThat(service.resolveMultiplier(25, 100))
                .isEqualByComparingTo("1.00");
        assertThat(service.resolveMultiplier(60, 100))
                .isEqualByComparingTo("1.10");
        assertThat(service.resolveMultiplier(90, 100))
                .isEqualByComparingTo("1.25");
    }
}