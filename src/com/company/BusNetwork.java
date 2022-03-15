package com.company;

import jdk.jshell.spi.ExecutionControl;

import java.util.List;

public class BusNetwork {
    private List<BusStop> busStops;
    private List<Route> routes;

    public List<Path> getPathsBetween(BusStop start, BusStop finish) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("This method has not been implemented yet");
    }
}
