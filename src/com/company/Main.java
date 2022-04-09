package com.company;
import java.util.List;
public class Main {

    public static void main(String[] args) {

        List<BusStop> busStops = BusStop.load();

        List<Route> routes = Route.load(busStops);


        BusNetwork network = new BusNetwork(busStops, routes);

        BusStop b1;
        BusStop b2;

        if(args.length != 2){
            b1 = network.findBusStop("POISY_COLLÈGE");
            b2 = network.findBusStop("Pommaries");
        }
        else {
            b1 = network.findBusStop(args[0]);
            b2 = network.findBusStop(args[1]);
        }

        List<Route> shortestPath = network.getPathBetween(b1, b2, Method.SHORTEST);
        List<Route> fastestPath = network.getPathBetween(b1, b2, Method.FASTEST);
        System.out.println(shortestPath);
        System.out.println(fastestPath);
    }
}