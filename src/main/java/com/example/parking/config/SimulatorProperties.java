package com.example.parking.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Stores simulator integration properties. */
@Validated
@ConfigurationProperties(prefix = "simulator")
public class SimulatorProperties {

    @NotBlank
    private String baseUrl;

    /** Returns the simulator base URL. */
    public String getBaseUrl() {
        return baseUrl;
    }

    /** Updates the simulator base URL. */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
