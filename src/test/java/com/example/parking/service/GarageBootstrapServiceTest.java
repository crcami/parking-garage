package com.example.parking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests the garage bootstrap service. */
@ExtendWith(MockitoExtension.class)
class GarageBootstrapServiceTest {

    @Mock
    private SimulatorClient simulatorClient;

    @Mock
    private GarageSectorRepository garageSectorRepository;

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    private GarageBootstrapService garageBootstrapService;

    /** Verifies that sectors and spots are correctly created from simulator data. */
    @Test
    void loadGarageConfiguration_shouldCreateSectorsAndSpots() {
        SimulatorGarageResponse response = buildSimulatorResponse();

        when(simulatorClient.getGarageConfiguration()).thenReturn(response);
        when(garageSectorRepository.findByCode("A")).thenReturn(Optional.empty());
        when(garageSectorRepository.save(any())).thenAnswer(inv -> {
            GarageSector s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });
        when(parkingSpotRepository.findByExternalSpotId(1L)).thenReturn(Optional.empty());
        when(garageSectorRepository.findByCode("A")).thenReturn(Optional.of(buildSector()));
        when(parkingSessionRepository.findAllByStatus(SessionStatus.OPEN)).thenReturn(List.of());

        garageBootstrapService.loadGarageConfiguration();

        verify(garageSectorRepository, times(1)).save(any(GarageSector.class));
        verify(parkingSpotRepository, times(1)).save(any(ParkingSpot.class));
    }

    /** Verifies that occupied is always reset to false on sync, regardless of previous state. */
    @Test
    void loadGarageConfiguration_shouldResetOccupiedOnSync() {
        ParkingSpot existingSpot = new ParkingSpot();
        existingSpot.setId(1L);
        existingSpot.setExternalSpotId(1L);
        existingSpot.setOccupied(true); // previously occupied

        SimulatorGarageResponse response = buildSimulatorResponse();

        when(simulatorClient.getGarageConfiguration()).thenReturn(response);
        when(garageSectorRepository.findByCode("A")).thenReturn(Optional.of(buildSector()));
        when(parkingSpotRepository.findByExternalSpotId(1L)).thenReturn(Optional.of(existingSpot));
        when(parkingSessionRepository.findAllByStatus(SessionStatus.OPEN)).thenReturn(List.of());

        garageBootstrapService.loadGarageConfiguration();

        ArgumentCaptor<ParkingSpot> captor = ArgumentCaptor.forClass(ParkingSpot.class);
        verify(parkingSpotRepository).save(captor.capture());
        assertThat(captor.getValue().isOccupied()).isFalse();
    }

    /** Verifies that stale open sessions are cancelled on startup. */
    @Test
    void loadGarageConfiguration_shouldCancelOpenSessions() {
        ParkingSession openSession = new ParkingSession();
        openSession.setStatus(SessionStatus.OPEN);

        SimulatorGarageResponse emptyResponse = new SimulatorGarageResponse();
        when(simulatorClient.getGarageConfiguration()).thenReturn(emptyResponse);
        when(parkingSessionRepository.findAllByStatus(SessionStatus.OPEN))
            .thenReturn(List.of(openSession));

        garageBootstrapService.loadGarageConfiguration();

        verify(parkingSessionRepository).saveAll(any());
        assertThat(openSession.getStatus()).isEqualTo(SessionStatus.CLOSED);
    }

    // --- Helpers ---

    private GarageSector buildSector() {
        GarageSector sector = new GarageSector();
        sector.setId(1L);
        sector.setCode("A");
        sector.setBasePrice(new BigDecimal("40.50"));
        sector.setMaxCapacity(10);
        return sector;
    }

    private SimulatorGarageResponse buildSimulatorResponse() {
        SimulatorGarageSectorResponse sectorDto = new SimulatorGarageSectorResponse();
        sectorDto.setSector("A");
        sectorDto.setBasePrice(new BigDecimal("40.50"));
        sectorDto.setMaxCapacity(10);

        SimulatorSpotResponse spotDto = new SimulatorSpotResponse();
        spotDto.setId(1L);
        spotDto.setSector("A");
        spotDto.setLat(-23.561684);
        spotDto.setLng(-46.655981);

        SimulatorGarageResponse response = new SimulatorGarageResponse();
        response.setGarage(List.of(sectorDto));
        response.setSpots(List.of(spotDto));
        return response;
    }
}
