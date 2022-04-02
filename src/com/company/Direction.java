package com.company;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Direction {
    private List<BusStop> busStops;
    private List<BusRoute> busRoutes;

    public Direction(List<BusStop> busStops, List<BusRoute> busRoutes) {
        this.busStops = busStops;
        this.busRoutes = busRoutes;
    }

    public int pathExistsBetween(BusStop startingStop, BusStop endStop){
        // verify if we can reach "to" from the "from" bus stop
        // thanks to the Breadth-first search (parcours en largeur)
        List<BusRoute> routes = new ArrayList<>(getRoutesFrom(startingStop));
        boolean continueLoop = true;
        int iteration=1;
        do {
            List<BusRoute> toBeAdded = new ArrayList<>();
            List<BusRoute> toBeRemoved = new ArrayList<>();

            if(routes.size() == 0){
                continueLoop = false;
            }
            for(BusRoute route : routes){
                if(route.getArrivalStop() == endStop){
                    return iteration;
                }
                else{
                    toBeAdded.addAll(getRoutesFrom(route.getArrivalStop()));
                    toBeRemoved.add(route);
                }
            }
            routes.addAll(toBeAdded);
            routes.removeAll(toBeRemoved);
            iteration ++;

        }while (continueLoop);

        return -1;
    }

    public List<BusRoute> getShortestPathBetween(BusStop from, BusStop to){
        // thanks to the Depth-first search (parcours en pronfondeur)
        List<BusRoute> routes = new ArrayList<>();
        boolean continueLoop=true;
        List<BusRoute> rs = getRoutesFrom(from);

        do{
            for(BusRoute r : rs){
                if(this.pathExistsBetween(r.getDepartureStop(), to) > -1){
                    routes.add(r);
                }
                if(r.getArrivalStop() == to){
                    continueLoop=false;
                }
            }
            rs = getRoutesFrom(routes.get(routes.size()-1).getArrivalStop());

        }while(continueLoop);

        return routes;
    }

    public Instant firstBusAfter(Instant a){
        // will return how much time we will have to wait for the next bus on another line.
        return null;
    }

    private List<BusRoute> getRoutesFrom(BusStop busStop){
        List<BusRoute> routes = new ArrayList<>();
        for (BusRoute r : this.busRoutes){
            if(r.getDepartureStop() == busStop)
                routes.add(r);
        }
        return routes;
    }

    public BusStop getLastBusStop(){
        for(BusStop stop : this.busStops){
            boolean isLast = true;
            for (BusRoute route : this.busRoutes){

                if(route.getDepartureStop().equals(stop)){
                    isLast = false;
                }
            }
            if(isLast) {
                return stop;
            }
        }
        return null;
    }
}