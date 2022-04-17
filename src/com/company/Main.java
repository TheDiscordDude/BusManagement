package com.company;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<BusStop> busStops = BusStop.load();

        BusNetwork network = new BusNetwork(busStops);

        BusStop b1;
        BusStop b2;
        Date departureTime;

        b1 = network.findBusStop(args[0]);
        b2 = network.findBusStop(args[1]);

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

        for(Route r : routes){
            System.out.println(r);
        }
        //System.out.println(routes);

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