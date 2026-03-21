package com.example.parking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.parking.dto.webhook.WebhookEventRequest;
import com.example.parking.dto.webhook.WebhookEventType;
import com.example.parking.entity.GarageSector;
import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.ParkingSpot;
import com.example.parking.entity.SessionStatus;
import com.example.parking.exception.BusinessException;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.ParkingSpotRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests webhook event handling logic. */
@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private PricingPolicyService pricingPolicyService;

    @InjectMocks
    private WebhookService webhookService;

    private GarageSector sectorA;
    private ParkingSpot freeSpot;

    @BeforeEach
    void setUp() {
        sectorA = new GarageSector();
        sectorA.setId(1L);
        sectorA.setCode("A");
        sectorA.setBasePrice(new BigDecimal("40.50"));
        sectorA.setMaxCapacity(10);

        freeSpot = new ParkingSpot();
        freeSpot.setId(1L);
        freeSpot.setExternalSpotId(1L);
        freeSpot.setSector(sectorA);
        freeSpot.setLatitude(-23.561684);
        freeSpot.setLongitude(-46.655981);
        freeSpot.setOccupied(false);
    }

    /** Verifies that an ENTRY event creates a session and marks the spot as occupied. */
    @Test
    void handleEntry_shouldCreateSessionAndMarkSpotOccupied() {
        WebhookEventRequest request = entryRequest("ABC-1234");

        when(parkingSessionRepository.findByLicensePlateAndStatus("ABC-1234", SessionStatus.OPEN))
            .thenReturn(Optional.empty());
        when(parkingSpotRepository.findByOccupiedFalseOrderBySector_CodeAscExternalSpotIdAsc())
            .thenReturn(List.of(freeSpot));
        when(parkingSpotRepository.countBySector_CodeAndOccupiedTrue("A")).thenReturn(0L);
        when(pricingPolicyService.resolveMultiplier(0L, 10)).thenReturn(new BigDecimal("0.90"));
        when(pricingPolicyService.resolveHourlyRate(any(), any())).thenReturn(new BigDecimal("36.45"));

        webhookService.handle(request);

        ArgumentCaptor<ParkingSpot> spotCaptor = ArgumentCaptor.forClass(ParkingSpot.class);
        verify(parkingSpotRepository).save(spotCaptor.capture());
        assertThat(spotCaptor.getValue().isOccupied()).isTrue();

        ArgumentCaptor<ParkingSession> sessionCaptor = ArgumentCaptor.forClass(ParkingSession.class);
        verify(parkingSessionRepository).save(sessionCaptor.capture());
        assertThat(sessionCaptor.getValue().getLicensePlate()).isEqualTo("ABC-1234");
        assertThat(sessionCaptor.getValue().getStatus()).isEqualTo(SessionStatus.OPEN);
    }

    /** Verifies that ENTRY fails when all spots are occupied. */
    @Test
    void handleEntry_shouldThrowWhenGarageIsFull() {
        WebhookEventRequest request = entryRequest("ABC-1234");

        when(parkingSessionRepository.findByLicensePlateAndStatus("ABC-1234", SessionStatus.OPEN))
            .thenReturn(Optional.empty());
        when(parkingSpotRepository.findByOccupiedFalseOrderBySector_CodeAscExternalSpotIdAsc())
            .thenReturn(List.of());

        assertThatThrownBy(() -> webhookService.handle(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("full");
    }

    /** Verifies that ENTRY fails when vehicle already has an open session. */
    @Test
    void handleEntry_shouldThrowWhenVehicleAlreadyHasOpenSession() {
        WebhookEventRequest request = entryRequest("ABC-1234");
        ParkingSession existing = new ParkingSession();
        existing.setStatus(SessionStatus.OPEN);

        when(parkingSessionRepository.findByLicensePlateAndStatus("ABC-1234", SessionStatus.OPEN))
            .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> webhookService.handle(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("already has an open session");
    }

    /** Verifies that a PARKED event updates the session spot by coordinates. */
    @Test
    void handleParked_shouldUpdateSpotByCoordinates() {
        ParkingSession session = openSession("XYZ-5678");

        ParkingSpot targetSpot = new ParkingSpot();
        targetSpot.setId(2L);
        targetSpot.setSector(sectorA);
        targetSpot.setOccupied(false);

        WebhookEventRequest request = parkedRequest("XYZ-5678", -23.561664, -46.655961);

        when(parkingSessionRepository.findByLicensePlateAndStatus("XYZ-5678", SessionStatus.OPEN))
            .thenReturn(Optional.of(session));
        when(parkingSpotRepository.findFirstByLatitudeBetweenAndLongitudeBetween(
            any(), any(), any(), any()
        )).thenReturn(Optional.of(targetSpot));

        webhookService.handle(request);

        verify(parkingSessionRepository).save(session);
        assertThat(session.getSpot()).isEqualTo(targetSpot);
    }

    /** Verifies that an EXIT event closes the session, calculates the charge and frees the spot. */
    @Test
    void handleExit_shouldCloseSessionAndCalculateCharge() {
        Instant entryTime = Instant.parse("2026-03-21T08:00:00Z");
        Instant exitTime = Instant.parse("2026-03-21T10:00:00Z");

        ParkingSession session = openSession("DEF-9999");
        session.setEntryTime(entryTime);
        session.setHourlyRateSnapshot(new BigDecimal("40.50"));
        session.setSpot(freeSpot);
        freeSpot.setOccupied(true);

        when(parkingSessionRepository.findByLicensePlateAndStatus("DEF-9999", SessionStatus.OPEN))
            .thenReturn(Optional.of(session));
        when(pricingPolicyService.calculateCharge(any(), any())).thenReturn(new BigDecimal("81.00"));

        WebhookEventRequest request = exitRequest("DEF-9999", exitTime);
        webhookService.handle(request);

        assertThat(session.getStatus()).isEqualTo(SessionStatus.CLOSED);
        assertThat(session.getAmountCharged()).isEqualByComparingTo("81.00");
        assertThat(session.getSpot().isOccupied()).isFalse();
    }

    /** Verifies that EXIT fails when exit time is before entry time. */
    @Test
    void handleExit_shouldThrowWhenExitBeforeEntry() {
        Instant entryTime = Instant.parse("2026-03-21T10:00:00Z");
        Instant exitTime = Instant.parse("2026-03-21T08:00:00Z");

        ParkingSession session = openSession("GHI-0000");
        session.setEntryTime(entryTime);

        when(parkingSessionRepository.findByLicensePlateAndStatus("GHI-0000", SessionStatus.OPEN))
            .thenReturn(Optional.of(session));

        assertThatThrownBy(() -> webhookService.handle(exitRequest("GHI-0000", exitTime)))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Exit time cannot be before entry time");

        verify(parkingSessionRepository, never()).save(any());
    }

    // --- Helpers ---

    private WebhookEventRequest entryRequest(String plate) {
        WebhookEventRequest r = new WebhookEventRequest();
        r.setEventType(WebhookEventType.ENTRY);
        r.setLicensePlate(plate);
        r.setEntryTime(Instant.parse("2026-03-21T08:00:00Z"));
        return r;
    }

    private WebhookEventRequest parkedRequest(String plate, double lat, double lng) {
        WebhookEventRequest r = new WebhookEventRequest();
        r.setEventType(WebhookEventType.PARKED);
        r.setLicensePlate(plate);
        r.setLat(lat);
        r.setLng(lng);
        return r;
    }

    private WebhookEventRequest exitRequest(String plate, Instant exitTime) {
        WebhookEventRequest r = new WebhookEventRequest();
        r.setEventType(WebhookEventType.EXIT);
        r.setLicensePlate(plate);
        r.setExitTime(exitTime);
        return r;
    }

    private ParkingSession openSession(String plate) {
        ParkingSession s = new ParkingSession();
        s.setLicensePlate(plate);
        s.setStatus(SessionStatus.OPEN);
        s.setSectorCode("A");
        s.setHourlyRateSnapshot(new BigDecimal("40.50"));
        s.setPricingMultiplier(new BigDecimal("1.00"));
        return s;
    }
}
