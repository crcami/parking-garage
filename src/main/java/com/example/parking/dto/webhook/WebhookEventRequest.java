package com.example.parking.dto.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/** Represents an incoming webhook payload. */
public class WebhookEventRequest {

    @NotBlank
    @JsonProperty("license_plate")
    private String licensePlate;

    @JsonProperty("entry_time")
    private Instant entryTime;

    @JsonProperty("exit_time")
    private Instant exitTime;

    private Double lat;

    private Double lng;

    @NotNull
    @JsonProperty("event_type")
    private WebhookEventType eventType;

    /** Returns the vehicle license plate. */
    public String getLicensePlate() {
        return licensePlate;
    }

    /** Updates the vehicle license plate. */
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /** Returns the entry timestamp. */
    public Instant getEntryTime() {
        return entryTime;
    }

    /** Updates the entry timestamp. */
    public void setEntryTime(Instant entryTime) {
        this.entryTime = entryTime;
    }

    /** Returns the exit timestamp. */
    public Instant getExitTime() {
        return exitTime;
    }

    /** Updates the exit timestamp. */
    public void setExitTime(Instant exitTime) {
        this.exitTime = exitTime;
    }

    /** Returns the event latitude. */
    public Double getLat() {
        return lat;
    }

    /** Updates the event latitude. */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /** Returns the event longitude. */
    public Double getLng() {
        return lng;
    }

    /** Updates the event longitude. */
    public void setLng(Double lng) {
        this.lng = lng;
    }

    /** Returns the event type. */
    public WebhookEventType getEventType() {
        return eventType;
    }

    /** Updates the event type. */
    public void setEventType(WebhookEventType eventType) {
        this.eventType = eventType;
    }
}
