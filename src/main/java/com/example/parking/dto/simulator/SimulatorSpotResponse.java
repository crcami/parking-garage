package com.example.parking.dto.simulator;

/** Represents a simulator parking spot payload. */
public class SimulatorSpotResponse {

    private Long id;
    private String sector;
    private Double lat;
    private Double lng;

    /** Returns the external spot ID. */
    public Long getId() {
        return id;
    }

    /** Updates the external spot ID. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Returns the spot sector. */
    public String getSector() {
        return sector;
    }

    /** Updates the spot sector. */
    public void setSector(String sector) {
        this.sector = sector;
    }

    /** Returns the spot latitude. */
    public Double getLat() {
        return lat;
    }

    /** Updates the spot latitude. */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /** Returns the spot longitude. */
    public Double getLng() {
        return lng;
    }

    /** Updates the spot longitude. */
    public void setLng(Double lng) {
        this.lng = lng;
    }
}
