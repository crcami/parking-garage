package com.example.parking;

import com.example.parking.config.SimulatorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/** Starts the parking garage application. */
@SpringBootApplication
@EnableConfigurationProperties(SimulatorProperties.class)
public class ParkingGarageApplication {

    /** Runs the Spring Boot application. */
    public static void main(String[] args) {
        SpringApplication.run(ParkingGarageApplication.class, args);
    }
}
