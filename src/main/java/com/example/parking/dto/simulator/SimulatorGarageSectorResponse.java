package com.example.parking.dto.simulator;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/** Represents a simulator sector payload. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimulatorGarageSectorResponse {

    private String sector;

    @JsonAlias({ "basePrice", "base_price" })
    private BigDecimal basePrice;

    @JsonAlias({ "maxCapacity", "max_capacity" })
    private Integer maxCapacity;


    /** Returns the sector code. */
    public String getSector() {
        return sector;
    }

    /** Updates the sector code. */
    public void setSector(String sector) {
        this.sector = sector;
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
}
