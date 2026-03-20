package com.example.parking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/** Stores a logical garage sector. */
@Entity
@Table(name = "garage_sectors")
public class GarageSector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /** Updates timestamps before entity updates. */
    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    /** Returns the sector database ID. */
    public Long getId() {
        return id;
    }

    /** Updates the sector database ID. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Returns the sector code. */
    public String getCode() {
        return code;
    }

    /** Updates the sector code. */
    public void setCode(String code) {
        this.code = code;
    }

    /** Returns the sector base price. */
    public BigDecimal getBasePrice() {
        return basePrice;
    }

    /** Updates the sector base price. */
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    /** Returns the sector max capacity. */
    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    /** Updates the sector max capacity. */
    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
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
