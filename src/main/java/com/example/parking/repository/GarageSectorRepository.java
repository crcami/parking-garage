package com.example.parking.repository;

import com.example.parking.entity.GarageSector;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persists garage sectors. */
public interface GarageSectorRepository extends JpaRepository<GarageSector, Long> {

    /** Finds a sector by code. */
    Optional<GarageSector> findByCode(String code);
}
