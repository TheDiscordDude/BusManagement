package com.company;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<BusStop> busStops = BusStop.load();

        BusNetwork network = new BusNetwork(busStops);

        BusStop start;
        BusStop destination;
        Date departureTime;

        start = network.findBusStop(args[0]);
        destination = network.findBusStop(args[1]);

        Calendar c1 = Calendar.getInstance();

        if(args.length >= 4){
            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(args[3]);
                c1.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(args.length >= 3){
            try {
                String[] parts = args[2].split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                c1.set(Calendar.HOUR_OF_DAY, hours);
                c1.set(Calendar.MINUTE, minutes);
                c1.set(Calendar.SECOND, 0);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        departureTime = c1.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(departureTime);
        List<Route> routes = Route.load(busStops, calendar);
        network.setRoutes(routes);

        List<Route> shortestPath = network.getPathBetween(start, destination, departureTime, Method.SHORTEST);
        List<Route> fastestPath = network.getPathBetween(start, destination, departureTime, Method.FASTEST);
        List<Route> farmostPath = network.getPathBetween(start, destination, departureTime, Method.FARMOST);

        System.out.println("Shortest Path to " + destination);
        for(Route r : shortestPath){
            System.out.println("\t"+r);
        }
        System.out.println();


        System.out.println("Fastest Path to " + destination);
        for(Route r : fastestPath){
            System.out.println("\t"+r);
        }
        System.out.println();


        System.out.println("Farmost Path to " + destination);
        for(Route r : farmostPath){
            System.out.println("\t"+r);
        }
        System.out.println();

    }
}