package com.example.parking.repository;

import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.SessionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persists parking sessions. */
public interface ParkingSessionRepository
    extends JpaRepository<ParkingSession, Long> {

    /** Finds the open session for a license plate. */
    Optional<ParkingSession> findByLicensePlateAndStatus(
        String licensePlate,
        SessionStatus status
    );

    /** Sums closed session revenue by exit date and sector. */
    @Query("""
        select coalesce(sum(ps.amountCharged), 0)
        from ParkingSession ps
        where ps.status = com.example.parking.entity.SessionStatus.CLOSED
          and ps.sectorCode = :sectorCode
          and ps.exitTime >= :start
          and ps.exitTime < :end
        """)
    BigDecimal sumRevenueByExitDateAndSector(
        @Param("sectorCode") String sectorCode,
        @Param("start") Instant start,
        @Param("end") Instant end
    );
}
