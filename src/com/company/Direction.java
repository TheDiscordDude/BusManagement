package com.company;

import java.time.Instant;
import java.util.List;

public class Direction {
    private List<BusStop> busStops;
    private List<BusRoute> busRoutes;

    public Direction(List<BusStop> busStops, List<BusRoute> busRoutes) {
        this.busStops = busStops;
        this.busRoutes = busRoutes;
    }

    public List<BusRoute> routeBetween(BusStop from, BusStop to){
        // verify if we can reach "to" from the "from" bus stop
        return null;
    }

    public Instant firstBusAfter(Instant a){
        // will return how much time we will have to wait for the next bus on another line.
        return null;
    }

    public BusStop getLastBusStop(){
        for(BusStop stop : this.busStops){
            boolean isLast = true;
            for (BusRoute route : this.busRoutes){

                if(route.getFromStop().equals(stop)){
                    isLast = false;
                }
            }
            System.out.println(stop.toString() + " " + isLast );

            if(isLast) {
                return stop;
            }
        }
        return null;
    }
}
