package com.company;
import java.util.List;
public class Main {

    public static void main(String[] args) {

        List<BusStop> busStops = BusStop.load();

        List<Route> routes = Route.load(busStops);

        BusNetwork network = new BusNetwork(busStops, routes);

        List<Route> path = network.getPathBetween(busStops.get(1), busStops.get(20), Method.SHORTEST);
        System.out.println(path);
    }
}