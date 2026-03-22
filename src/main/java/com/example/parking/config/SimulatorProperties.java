package com.example.parking.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Stores simulator integration properties. */
@Validated
@ConfigurationProperties(prefix = "simulator")
public class SimulatorProperties {

    @NotBlank
    private String baseUrl;

    @Min(1)
    private int startupMaxAttempts = 30;

    @Min(100)
    private long startupDelayMillis = 2000L;

    /** Returns the simulator base URL. */
    public String getBaseUrl() {
        return baseUrl;
    }

    /** Updates the simulator base URL. */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /** Returns the startup max attempts. */
    public int getStartupMaxAttempts() {
        return startupMaxAttempts;
    }

    /** Updates the startup max attempts. */
    public void setStartupMaxAttempts(int startupMaxAttempts) {
        this.startupMaxAttempts = startupMaxAttempts;
    }

    /** Returns the startup delay in milliseconds. */
    public long getStartupDelayMillis() {
        return startupDelayMillis;
    }

    /** Updates the startup delay in milliseconds. */
    public void setStartupDelayMillis(long startupDelayMillis) {
        this.startupDelayMillis = startupDelayMillis;
    }
}