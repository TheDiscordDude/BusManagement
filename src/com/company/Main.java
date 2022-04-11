package com.company;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
public class Main {

    public static void main(String[] args) {

        List<BusStop> busStops = BusStop.load();

        List<Route> routes = Route.load(busStops);

        BusNetwork network = new BusNetwork(busStops, routes);

        BusStop b1;
        BusStop b2;
        Date departureTime = Date.from(Instant.now());


        switch (args.length){
            case 2:{
                b1 = network.findBusStop(args[0]);
                b2 = network.findBusStop(args[1]);
                try{
                    departureTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm").parse(args[2]);
                } catch (ParseException e){
                    e.printStackTrace();
                    departureTime = Date.from(Instant.now());
                }
                break;
            }
            default:
                b1 = network.findBusStop("POISY_COLLÈGE");
                b2 = network.findBusStop("Pommaries");
                break;
        }

        System.out.println(routes.get(0).getStartingPoint());
        System.out.println(routes.get(0).getDestination());
        System.out.println(routes.get(0).getDepartureTimes());
        System.out.println(routes.get(0).getArrivalTimes());

        List<Route> shortestPath = network.getPathBetween(b1, b2, departureTime, Method.SHORTEST);
        List<Route> fastestPath = network.getPathBetween(b1, b2, departureTime, Method.FASTEST);
        System.out.println(shortestPath);
        System.out.println(fastestPath);
    }
}