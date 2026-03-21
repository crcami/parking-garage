package com.example.parking.service;

import com.example.parking.dto.simulator.SimulatorGarageResponse;
import com.example.parking.dto.simulator.SimulatorGarageSectorResponse;
import com.example.parking.dto.simulator.SimulatorSpotResponse;
import com.example.parking.entity.GarageSector;
import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.ParkingSpot;
import com.example.parking.entity.SessionStatus;
import com.example.parking.integration.SimulatorClient;
import com.example.parking.repository.GarageSectorRepository;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.ParkingSpotRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Synchronizes the garage configuration with the simulator. */
@Service
public class GarageBootstrapService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        GarageBootstrapService.class
    );

    private final SimulatorClient simulatorClient;
    private final GarageSectorRepository garageSectorRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSessionRepository parkingSessionRepository;

    /** Creates the garage bootstrap service. */
    public GarageBootstrapService(
        SimulatorClient simulatorClient,
        GarageSectorRepository garageSectorRepository,
        ParkingSpotRepository parkingSpotRepository,
        ParkingSessionRepository parkingSessionRepository
    ) {
        this.simulatorClient = simulatorClient;
        this.garageSectorRepository = garageSectorRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingSessionRepository = parkingSessionRepository;
    }

    /** Loads the simulator garage configuration into the database. */
    @Transactional
    public void loadGarageConfiguration() {
        cancelOpenSessions();

        SimulatorGarageResponse response = simulatorClient.getGarageConfiguration();

        if (response == null) {
            LOGGER.warn("Simulator garage response is null.");
            return;
        }

        if (response.getGarage() != null) {
            response.getGarage().forEach(this::upsertSector);
        }

        if (response.getSpots() != null) {
            response.getSpots().forEach(this::upsertSpot);
        }

        LOGGER.info("Garage configuration synchronized successfully.");
    }

    /**
     * Cancels all open sessions on startup to avoid stale state.
     * The simulator is the source of truth; the webhook will re-notify active vehicles.
     */
    private void cancelOpenSessions() {
        List<ParkingSession> openSessions = parkingSessionRepository.findAllByStatus(
            SessionStatus.OPEN
        );

        if (!openSessions.isEmpty()) {
            LOGGER.warn(
                "Cancelling {} stale open session(s) from previous run.",
                openSessions.size()
            );

            openSessions.forEach(session -> session.setStatus(SessionStatus.CLOSED));
            parkingSessionRepository.saveAll(openSessions);
        }
    }

    /** Upserts one garage sector. */
    private void upsertSector(SimulatorGarageSectorResponse source) {
        GarageSector sector = garageSectorRepository.findByCode(source.getSector())
            .orElseGet(GarageSector::new);

        sector.setCode(source.getSector());
        sector.setBasePrice(source.getBasePrice());
        sector.setMaxCapacity(source.getMaxCapacity());

        garageSectorRepository.save(sector);
    }

    /** Upserts one parking spot and always resets occupied to false on sync. */
    private void upsertSpot(SimulatorSpotResponse source) {
        GarageSector sector = garageSectorRepository.findByCode(source.getSector())
            .orElseThrow(() -> new IllegalStateException(
                "Sector must exist before loading spots."
            ));

        ParkingSpot spot = parkingSpotRepository.findByExternalSpotId(source.getId())
            .orElseGet(ParkingSpot::new);

        spot.setExternalSpotId(source.getId());
        spot.setSector(sector);
        spot.setLatitude(source.getLat());
        spot.setLongitude(source.getLng());
        // Always reset to false on sync: the simulator is the source of truth.
        // Active vehicles will be re-notified via webhook after restart.
        spot.setOccupied(false);

        parkingSpotRepository.save(spot);
    }
}
