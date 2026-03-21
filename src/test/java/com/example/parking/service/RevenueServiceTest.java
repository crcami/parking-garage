package com.example.parking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.parking.dto.revenue.RevenueResponse;
import com.example.parking.repository.ParkingSessionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests revenue aggregation logic. */
@ExtendWith(MockitoExtension.class)
class RevenueServiceTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    private RevenueService revenueService;

    /** Verifies that the service returns the sum from the repository. */
    @Test
    void getRevenue_shouldReturnSumForDateAndSector() {
        LocalDate date = LocalDate.of(2026, 3, 21);
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        when(parkingSessionRepository.sumRevenueByExitDateAndSector("A", start, end))
            .thenReturn(new BigDecimal("81.00"));

        RevenueResponse response = revenueService.getRevenue(date, "A");

        assertThat(response.amount()).isEqualByComparingTo("81.00");
        assertThat(response.currency()).isEqualTo("BRL");
    }

    /** Verifies that the service returns zero when there are no closed sessions. */
    @Test
    void getRevenue_shouldReturnZeroWhenNoSessions() {
        LocalDate date = LocalDate.of(2026, 3, 21);
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        when(parkingSessionRepository.sumRevenueByExitDateAndSector("B", start, end))
            .thenReturn(BigDecimal.ZERO);

        RevenueResponse response = revenueService.getRevenue(date, "B");

        assertThat(response.amount()).isEqualByComparingTo("0");
    }
}
