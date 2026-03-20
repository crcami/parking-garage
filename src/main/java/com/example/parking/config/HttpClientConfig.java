package com.example.parking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Configures HTTP clients for integrations. */
@Configuration
public class HttpClientConfig {

    /** Creates the simulator REST client. */
    @Bean
    public RestClient simulatorRestClient(SimulatorProperties properties) {
        return RestClient.builder()
            .baseUrl(properties.getBaseUrl())
            .build();
    }
}
