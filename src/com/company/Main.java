package com.company;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<BusStop> busStops = BusStop.load();
        /*
        for(BusStop b : busStops){
            System.out.println(b);
        }
        */


        List<Route> routes = Route.load(busStops);
        /*
        for(Route r : routes){
            System.out.println(r);
        }

         */

        BusNetwork network = new BusNetwork(busStops, routes);

        List<Route> path = network.getPathBetween(busStops.get(1), busStops.get(20), Method.SHORTEST);
        System.out.println(path);
    }

}
