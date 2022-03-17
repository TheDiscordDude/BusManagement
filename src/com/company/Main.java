package com.company;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	    // Create all the Bus stops here
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        //LocalTime a = LocalTime.parse("7:45", formatter);
        //System.out.println(a);


        List<BusStop> busStops = BusStop.load();

        for(BusStop b : busStops){
            System.out.println(b);
        }

        List<Route> routes = Route.load(busStops);
        for(Route r : routes){
            System.out.println(r);
        }
    }

}
