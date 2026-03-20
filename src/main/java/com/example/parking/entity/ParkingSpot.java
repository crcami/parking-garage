package com.example.parking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

/** Stores a physical parking spot. */
@Entity
@Table(name = "parking_spots")
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_spot_id", nullable = false, unique = true)
    private Long externalSpotId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sector_id", nullable = false)
    private GarageSector sector;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private boolean occupied;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /** Updates timestamps before entity updates. */
    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    /** Returns the spot database ID. */
    public Long getId() {
        return id;
    }

    /** Updates the spot database ID. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Returns the external spot ID. */
    public Long getExternalSpotId() {
        return externalSpotId;
    }

    /** Updates the external spot ID. */
    public void setExternalSpotId(Long externalSpotId) {
        this.externalSpotId = externalSpotId;
    }

    /** Returns the spot sector. */
    public GarageSector getSector() {
        return sector;
    }

    /** Updates the spot sector. */
    public void setSector(GarageSector sector) {
        this.sector = sector;
    }

    /** Returns the spot latitude. */
    public Double getLatitude() {
        return latitude;
    }

    /** Updates the spot latitude. */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /** Returns the spot longitude. */
    public Double getLongitude() {
        return longitude;
    }

    /** Updates the spot longitude. */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /** Returns whether the spot is occupied. */
    public boolean isOccupied() {
        return occupied;
    }

    /** Updates the occupied flag. */
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    /** Returns the creation timestamp. */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** Updates the creation timestamp. */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /** Returns the update timestamp. */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /** Updates the update timestamp. */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
