package com.example.parking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/** Stores one vehicle parking lifecycle. */
@Entity
@Table(name = "parking_sessions")
public class ParkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    @Column(name = "sector_code", nullable = false, length = 30)
    private String sectorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    private ParkingSpot spot;

    @Column(name = "entry_time", nullable = false)
    private Instant entryTime;

    @Column(name = "exit_time")
    private Instant exitTime;

    @Column(name = "pricing_multiplier", nullable = false, precision = 5, scale = 2)
    private BigDecimal pricingMultiplier;

    @Column(
        name = "hourly_rate_snapshot",
        nullable = false,
        precision = 10,
        scale = 2
    )
    private BigDecimal hourlyRateSnapshot;

    @Column(name = "amount_charged", precision = 10, scale = 2)
    private BigDecimal amountCharged;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /** Updates timestamps before entity updates. */
    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    /** Returns the session database ID. */
    public Long getId() {
        return id;
    }

    /** Updates the session database ID. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Returns the vehicle license plate. */
    public String getLicensePlate() {
        return licensePlate;
    }

    /** Updates the vehicle license plate. */
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /** Returns the assigned sector code. */
    public String getSectorCode() {
        return sectorCode;
    }

    /** Updates the assigned sector code. */
    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

    /** Returns the assigned spot. */
    public ParkingSpot getSpot() {
        return spot;
    }

    /** Updates the assigned spot. */
    public void setSpot(ParkingSpot spot) {
        this.spot = spot;
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

    /** Returns the pricing multiplier. */
    public BigDecimal getPricingMultiplier() {
        return pricingMultiplier;
    }

    /** Updates the pricing multiplier. */
    public void setPricingMultiplier(BigDecimal pricingMultiplier) {
        this.pricingMultiplier = pricingMultiplier;
    }

    /** Returns the frozen hourly rate. */
    public BigDecimal getHourlyRateSnapshot() {
        return hourlyRateSnapshot;
    }

    /** Updates the frozen hourly rate. */
    public void setHourlyRateSnapshot(BigDecimal hourlyRateSnapshot) {
        this.hourlyRateSnapshot = hourlyRateSnapshot;
    }

    /** Returns the charged amount. */
    public BigDecimal getAmountCharged() {
        return amountCharged;
    }

    /** Updates the charged amount. */
    public void setAmountCharged(BigDecimal amountCharged) {
        this.amountCharged = amountCharged;
    }

    /** Returns the session status. */
    public SessionStatus getStatus() {
        return status;
    }

    /** Updates the session status. */
    public void setStatus(SessionStatus status) {
        this.status = status;
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
