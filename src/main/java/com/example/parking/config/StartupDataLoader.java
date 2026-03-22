package com.example.parking.config;

import com.example.parking.service.GarageBootstrapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Loads garage data when the application starts. */
@Component
public class StartupDataLoader implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        StartupDataLoader.class
    );

    private final GarageBootstrapService garageBootstrapService;
    private final SimulatorProperties simulatorProperties;

    /** Creates the startup loader. */
    public StartupDataLoader(
        GarageBootstrapService garageBootstrapService,
        SimulatorProperties simulatorProperties
    ) {
        this.garageBootstrapService = garageBootstrapService;
        this.simulatorProperties = simulatorProperties;
    }

    /** Executes the initial garage synchronization. */
    @Override
    public void run(ApplicationArguments args) {
        int maxAttempts = simulatorProperties.getStartupMaxAttempts();
        long delayMillis = simulatorProperties.getStartupDelayMillis();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                LOGGER.info(
                    "Loading garage configuration from simulator. Attempt {}/{}.",
                    attempt,
                    maxAttempts
                );

                garageBootstrapService.loadGarageConfiguration();

                LOGGER.info("Garage configuration loaded successfully.");
                return;
            } catch (Exception exception) {
                if (attempt == maxAttempts) {
                    throw new IllegalStateException(
                        "Could not load garage configuration from simulator "
                            + "after " + maxAttempts + " attempts.",
                        exception
                    );
                }

                LOGGER.warn(
                    "Could not load garage configuration from simulator on "
                        + "attempt {}/{}. Waiting {} ms before retrying.",
                    attempt,
                    maxAttempts,
                    delayMillis,
                    exception
                );

                sleep(delayMillis);
            }
        }
    }

    /** Sleeps between startup retry attempts. */
    private void sleep(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                "Startup retry was interrupted.",
                exception
            );
        }
    }
}