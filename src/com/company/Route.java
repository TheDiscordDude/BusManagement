package com.company;

import java.time.Duration;
import java.time.Instant;

public class Route {
    private BusStop fromStop;
    private BusStop toStop;
    private Instant departureTime;
    private Instant arrivalTime;
    private String busLine;

    public Route(BusStop fromStop, BusStop toStop, Instant departureTime, Instant arrivalTime, String busLine) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.busLine = busLine;
    }

    public Duration getWeight(){
        return Duration.between(this.arrivalTime,this.departureTime);
    }
}
