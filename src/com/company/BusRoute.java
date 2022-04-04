package com.company;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BusRoute {
    private BusStop departureStop;
    private BusStop arrivalStop;
    private long weight;

    public BusRoute(BusStop fromStop, BusStop toStop, long weight) {
        this.departureStop = fromStop;
        this.arrivalStop = toStop;
        this.weight = weight;
    }

    public BusRoute(BusStop fromStop, BusStop toStop) {
        this(fromStop, toStop, 1);
    }

    public BusRoute(){
        this.departureStop = null;
        this.arrivalStop = null;
    }



    public BusRoute(BusRoute busRoute){
        this(busRoute.getDepartureStop(), busRoute.getArrivalStop(), busRoute.getWeight());
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public BusStop getDepartureStop() {
        return departureStop;
    }

    public void setDepartureStop(BusStop departureStop) {
        this.departureStop = departureStop;
    }

    public BusStop getArrivalStop() {
        return arrivalStop;
    }

    public void setArrivalStop(BusStop arrivalStop) {
        this.arrivalStop = arrivalStop;
    }

    public static List<BusRoute> load(List<BusStop> busStops, String filePath){
        List<BusRoute> routes = new ArrayList<BusRoute>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            String firstLine = lines.get(0);
            String[] busStopsNames = firstLine.split(" N ");
            for (int i = 0; i < busStopsNames.length-1 ;i ++) {
                List<BusRoute> inDepthRoutes = new ArrayList<BusRoute>();
                String[] departures = busStopsNames[i].split(" \\+ ");
                String[] arrivals = busStopsNames[i+1].split(" \\+ ");
                for(String departure : departures){
                    BusStop busStop = null;
                    for(BusStop b : busStops){
                        if (Objects.equals(b.getName(), departure)){
                            busStop = b;
                        }
                    }
                    BusRoute currentRoute = new BusRoute();
                    currentRoute.setDepartureStop(busStop);
                    inDepthRoutes.add(currentRoute);
                }
                for(String arrival : arrivals){
                    BusStop busStop = null;
                    for(BusStop b : busStops){
                        if (Objects.equals(b.getName(), arrival)){
                            busStop = b;
                        }
                    }
                    for(BusRoute r : inDepthRoutes){
                        if(r.getArrivalStop() == null){
                            r.arrivalStop =busStop;
                        }
                        else{
                            BusRoute newRoute = new BusRoute(r);
                            newRoute.setArrivalStop(busStop);
                            inDepthRoutes.add(newRoute);
                        }
                    }
                }
                routes.addAll(inDepthRoutes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    public BusRoute getReverse(){
        BusRoute reverse = new BusRoute(this);
        BusStop from = reverse.getDepartureStop();
        reverse.setDepartureStop(reverse.getArrivalStop());
        reverse.setArrivalStop(from);
        return reverse;
    }

    @Override
    public String toString() {
        return departureStop + " -> " + arrivalStop ;
    }
}
