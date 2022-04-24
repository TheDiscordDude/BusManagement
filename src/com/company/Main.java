package com.company;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Verifying if enough arguments are passed
        if(args.length < 2){
            System.out.println("IllegalArgumentException. Wrong arguments");
            help();
            System.exit(12);
        }

        // Loading the bus stops
        List<BusStop> busStops = BusStop.load();
        BusNetwork network = new BusNetwork(busStops);

        BusStop start;
        BusStop destination;
        Date departureTime;

        start = new BusStop(args[0]);
        destination = new BusStop(args[1]);
        if (!busStops.contains(start) || !busStops.contains(destination)){
            System.out.println("IllegalArgumentException. The bus stops you entered where not found");
            help();
            System.exit(11);
        }

        Calendar c1 = Calendar.getInstance();

        if(args.length >= 4){
            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(args[3]);
                c1.setTime(date);
            } catch (ParseException e) {
                System.out.println("ParseException. Please enter a valid date. For example : 30-12-2022");
                e.printStackTrace();
                System.exit(5);
            }
        }

        if(args.length >= 3){
            try {
                String[] parts = args[2].split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                if ( 0 > hours || hours > 23 || 0 > minutes || minutes > 59 )
                    throw new NumberFormatException("Wrong time format");
                c1.set(Calendar.HOUR_OF_DAY, hours);
                c1.set(Calendar.MINUTE, minutes);
                c1.set(Calendar.SECOND, 0);

            } catch (NumberFormatException e) {
                System.out.println("NumberFormatException. Please enter a valid time in 24h format. For example : 07:30");
                e.printStackTrace();
                System.exit(4);
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
        }        System.out.println();

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

    public static void help(){
        System.out.println("""
                Arguments :
                \tStarting_Point Destination Hour(optional) Date(optional)
                \tExample :\s
                \tGARE Pommaries 07:00 22-10-2022
                """);
    }
}