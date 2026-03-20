package com.example.parking;

import com.example.parking.config.SimulatorProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/** Starts the parking garage application. */
@SpringBootApplication
@EnableConfigurationProperties(SimulatorProperties.class)
@OpenAPIDefinition(
    info = @Info(
        title = "Parking Garage API",
        version = "v1",
        description = "Documentação oficial da API do Parking Garage. Aqui estão listados os endpoints para webhooks do simulador e consulta de faturamento."
    )
)
public class ParkingGarageApplication {

    /** Runs the Spring Boot application. */
    public static void main(String[] args) {
        SpringApplication.run(ParkingGarageApplication.class, args);
    }
}
