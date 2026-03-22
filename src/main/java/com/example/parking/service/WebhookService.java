package com.example.parking.service;

import com.example.parking.dto.webhook.WebhookEventRequest;
import com.example.parking.dto.webhook.WebhookEventType;
import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.ParkingSpot;
import com.example.parking.entity.SessionStatus;
import com.example.parking.exception.BusinessException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.ParkingSpotRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Processes webhook events from the simulator. */
@Service
public class WebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        WebhookService.class
    );

    private static final double COORDINATE_TOLERANCE = 0.000001d;

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final PricingPolicyService pricingPolicyService;

    /** Creates the webhook service. */
    public WebhookService(
        ParkingSpotRepository parkingSpotRepository,
        ParkingSessionRepository parkingSessionRepository,
        PricingPolicyService pricingPolicyService
    ) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingSessionRepository = parkingSessionRepository;
        this.pricingPolicyService = pricingPolicyService;
    }

    /** Routes a webhook request to the correct handler. */
    @Transactional
    public void handle(WebhookEventRequest request) {
        WebhookEventType eventType = request.getEventType();

        LOGGER.info(
            "Processing webhook event {} for plate {}.",
            eventType,
            request.getLicensePlate()
        );

        switch (eventType) {
            case ENTRY -> handleEntry(request);
            case PARKED -> handleParked(request);
            case EXIT -> handleExit(request);
            default -> throw new BusinessException("Unsupported event type.");
        }
    }

    /** Handles an ENTRY event. */
    private void handleEntry(WebhookEventRequest request) {
        requireEntryPayload(request);

        parkingSessionRepository.findByLicensePlateAndStatus(
            request.getLicensePlate(),
            SessionStatus.OPEN
        ).ifPresent(existing -> {
            throw new BusinessException("Vehicle already has an open session.");
        });

        ParkingSpot spot = resolveFirstAvailableSpot();
        long occupiedSpots = parkingSpotRepository.countBySector_CodeAndOccupiedTrue(
            spot.getSector().getCode()
        );

        BigDecimal multiplier = pricingPolicyService.resolveMultiplier(
            occupiedSpots,
            spot.getSector().getMaxCapacity()
        );

        BigDecimal hourlyRate = pricingPolicyService.resolveHourlyRate(
            spot.getSector().getBasePrice(),
            multiplier
        );

        spot.setOccupied(true);
        parkingSpotRepository.save(spot);

        ParkingSession session = new ParkingSession();
        session.setLicensePlate(request.getLicensePlate());
        session.setSectorCode(spot.getSector().getCode());
        session.setSpot(spot);
        session.setEntryTime(request.getEntryTime());
        session.setPricingMultiplier(multiplier);
        session.setHourlyRateSnapshot(hourlyRate);
        session.setStatus(SessionStatus.OPEN);

        parkingSessionRepository.save(session);

        LOGGER.info(
            "Vehicle {} entered sector {} on spot {}.",
            request.getLicensePlate(),
            spot.getSector().getCode(),
            spot.getExternalSpotId()
        );
    }

    /** Handles a PARKED event. */
    private void handleParked(WebhookEventRequest request) {
        requireParkedPayload(request);

        ParkingSession session = parkingSessionRepository.findByLicensePlateAndStatus(
            request.getLicensePlate(),
            SessionStatus.OPEN
        ).orElseThrow(() -> new ResourceNotFoundException(
            "Open session not found for vehicle."
        ));

        ParkingSpot actualSpot = resolveSpotByCoordinates(
            request.getLat(),
            request.getLng()
        );

        ParkingSpot currentSpot = session.getSpot();

        if (
            currentSpot != null &&
            currentSpot.getId().equals(actualSpot.getId())
        ) {
            LOGGER.info(
                "Vehicle {} is already parked on spot {}.",
                request.getLicensePlate(),
                actualSpot.getExternalSpotId()
            );
            return;
        }

        if (
            actualSpot.isOccupied() &&
            (currentSpot == null || !actualSpot.getId().equals(currentSpot.getId()))
        ) {
            throw new BusinessException("Target spot is already occupied.");
        }

        if (currentSpot != null) {
            currentSpot.setOccupied(false);
            parkingSpotRepository.save(currentSpot);
        }

        actualSpot.setOccupied(true);
        parkingSpotRepository.save(actualSpot);

        session.setSpot(actualSpot);
        session.setSectorCode(actualSpot.getSector().getCode());
        parkingSessionRepository.save(session);

        LOGGER.info(
            "Vehicle {} parked on sector {} spot {}.",
            request.getLicensePlate(),
            actualSpot.getSector().getCode(),
            actualSpot.getExternalSpotId()
        );
    }

    /** Handles an EXIT event. */
    private void handleExit(WebhookEventRequest request) {
        requireExitPayload(request);

        ParkingSession session = parkingSessionRepository.findByLicensePlateAndStatus(
            request.getLicensePlate(),
            SessionStatus.OPEN
        ).orElseThrow(() -> new ResourceNotFoundException(
            "Open session not found for vehicle."
        ));

        if (request.getExitTime().isBefore(session.getEntryTime())) {
            throw new BusinessException("Exit time cannot be before entry time.");
        }

        Duration duration = Duration.between(
            session.getEntryTime(),
            request.getExitTime()
        );

        BigDecimal amount = pricingPolicyService.calculateCharge(
            session.getHourlyRateSnapshot(),
            duration
        );

        session.setExitTime(request.getExitTime());
        session.setAmountCharged(amount);
        session.setStatus(SessionStatus.CLOSED);

        if (session.getSpot() != null) {
            ParkingSpot spot = session.getSpot();
            spot.setOccupied(false);
            parkingSpotRepository.save(spot);
        }

        parkingSessionRepository.save(session);

        LOGGER.info(
            "Vehicle {} exited with amount {}.",
            request.getLicensePlate(),
            amount
        );
    }

    /** Resolves the first free spot in deterministic order. */
    private ParkingSpot resolveFirstAvailableSpot() {
        List<ParkingSpot> spots = parkingSpotRepository
            .findByOccupiedFalseOrderBySector_CodeAscExternalSpotIdAsc();

        if (spots.isEmpty()) {
            throw new BusinessException(
                "Garage is full and cannot accept new entries."
            );
        }

        return spots.getFirst();
    }

    /** Resolves a spot using a coordinate tolerance. */
    private ParkingSpot resolveSpotByCoordinates(Double lat, Double lng) {
        return parkingSpotRepository.findFirstByLatitudeBetweenAndLongitudeBetween(
            lat - COORDINATE_TOLERANCE,
            lat + COORDINATE_TOLERANCE,
            lng - COORDINATE_TOLERANCE,
            lng + COORDINATE_TOLERANCE
        ).orElseThrow(() -> new ResourceNotFoundException(
            "Parking spot not found for the provided coordinates."
        ));
    }

    /** Validates the ENTRY payload. */
    private void requireEntryPayload(WebhookEventRequest request) {
        if (request.getEntryTime() == null) {
            throw new BusinessException("ENTRY event requires entry_time.");
        }
    }

    /** Validates the PARKED payload. */
    private void requireParkedPayload(WebhookEventRequest request) {
        if (request.getLat() == null || request.getLng() == null) {
            throw new BusinessException("PARKED event requires lat and lng.");
        }
    }

    /** Validates the EXIT payload. */
    private void requireExitPayload(WebhookEventRequest request) {
        if (request.getExitTime() == null) {
            throw new BusinessException("EXIT event requires exit_time.");
        }
    }
}