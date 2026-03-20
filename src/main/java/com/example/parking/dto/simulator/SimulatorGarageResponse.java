package com.example.parking.dto.simulator;

import java.util.List;

/** Represents the simulator garage configuration response. */
public class SimulatorGarageResponse {

    private List<SimulatorGarageSectorResponse> garage;
    private List<SimulatorSpotResponse> spots;

    /** Returns the garage sectors. */
    public List<SimulatorGarageSectorResponse> getGarage() {
        return garage;
    }

    /** Updates the garage sectors. */
    public void setGarage(List<SimulatorGarageSectorResponse> garage) {
        this.garage = garage;
    }

    /** Returns the garage spots. */
    public List<SimulatorSpotResponse> getSpots() {
        return spots;
    }

    /** Updates the garage spots. */
    public void setSpots(List<SimulatorSpotResponse> spots) {
        this.spots = spots;
    }
}
