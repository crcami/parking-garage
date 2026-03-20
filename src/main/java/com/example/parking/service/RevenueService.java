package com.example.parking.service;

import com.example.parking.dto.revenue.RevenueResponse;
import com.example.parking.repository.ParkingSessionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;

/** Aggregates revenue information. */
@Service
public class RevenueService {

    private final ParkingSessionRepository parkingSessionRepository;

    /** Creates the revenue service. */
    public RevenueService(ParkingSessionRepository parkingSessionRepository) {
        this.parkingSessionRepository = parkingSessionRepository;
    }

    /** Returns the revenue for one date and sector. */
    public RevenueResponse getRevenue(LocalDate date, String sector) {
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        BigDecimal amount = parkingSessionRepository.sumRevenueByExitDateAndSector(
            sector,
            start,
            end
        );

        return new RevenueResponse(amount, "BRL", Instant.now());
    }
}
