package com.example.parking.integration;

import com.example.parking.dto.simulator.SimulatorGarageResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Reads configuration data from the simulator. */
@Component
public class SimulatorClient {

    private final RestClient restClient;

    /** Creates the simulator client. */
    public SimulatorClient(RestClient simulatorRestClient) {
        this.restClient = simulatorRestClient;
    }

    /** Fetches the garage configuration. */
    public SimulatorGarageResponse getGarageConfiguration() {
        return restClient.get()
            .uri("/garage")
            .retrieve()
            .body(SimulatorGarageResponse.class);
    }
}
