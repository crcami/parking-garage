package com.example.parking.config;

import com.example.parking.service.GarageBootstrapService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Loads garage data when the application starts. */
@Component
public class StartupDataLoader implements ApplicationRunner {

    private final GarageBootstrapService garageBootstrapService;

    /** Creates the startup loader. */
    public StartupDataLoader(GarageBootstrapService garageBootstrapService) {
        this.garageBootstrapService = garageBootstrapService;
    }

    /** Executes the initial garage synchronization. */
    @Override
    public void run(ApplicationArguments args) {
        garageBootstrapService.loadGarageConfiguration();
    }
}
