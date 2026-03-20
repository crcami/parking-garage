package com.example.parking.repository;

import com.example.parking.entity.ParkingSpot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persists parking spots. */
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    /** Finds a spot by external simulator ID. */
    Optional<ParkingSpot> findByExternalSpotId(Long externalSpotId);

    /** Returns free spots ordered for deterministic assignment. */
    List<ParkingSpot> findByOccupiedFalseOrderBySector_CodeAscExternalSpotIdAsc();

    /** Counts occupied spots for a sector code. */
    long countBySector_CodeAndOccupiedTrue(String sectorCode);

    /** Finds the closest exact spot by coordinates range. */
    Optional<ParkingSpot> findFirstByLatitudeBetweenAndLongitudeBetween(
        Double minLatitude,
        Double maxLatitude,
        Double minLongitude,
        Double maxLongitude
    );
}
