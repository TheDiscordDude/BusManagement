package com.company;
import java.util.List;
public class Main {

    public static void main(String[] args) {

        List<BusStop> busStops = BusStop.load();

        List<Route> routes = Route.load(busStops);

        /*
        for(Route r : routes)
            System.out.println( r.toString() + r.getTravelTime());

         */

        BusNetwork network = new BusNetwork(busStops, routes);

        List<Route> shortestPath = network.getPathBetween(busStops.get(1), busStops.get(20), Method.SHORTEST);
        List<Route> fastestPath = network.getPathBetween(busStops.get(1), busStops.get(20), Method.SHORTEST);
        System.out.println(shortestPath);
        System.out.println(fastestPath);
    }
}