package com.company;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BusRoute {
    private BusStop fromStop;
    private BusStop toStop;
    private long weight;

    public BusRoute(BusStop fromStop, BusStop toStop, long weight) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.weight = weight;
    }

    public BusRoute(BusStop fromStop, BusStop toStop) {
        this(fromStop, toStop, 1);
    }

    public BusRoute(){
        this.fromStop = null;
        this.toStop = null;
    }



    public BusRoute(BusRoute busRoute){
        this(busRoute.getFromStop(), busRoute.getToStop(), busRoute.getWeight());
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public BusStop getFromStop() {
        return fromStop;
    }

    public void setFromStop(BusStop fromStop) {
        this.fromStop = fromStop;
    }

    public BusStop getToStop() {
        return toStop;
    }

    public void setToStop(BusStop toStop) {
        this.toStop = toStop;
    }

    public static List<BusRoute> load(List<BusStop> busStops, String pathToBusLine){
        List<String> filePaths = new ArrayList<String>();
        filePaths.add(pathToBusLine);
        List<BusRoute> routes = new ArrayList<BusRoute>();

        for (String filePath : filePaths) {
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
                        currentRoute.setFromStop(busStop);
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
                            if(r.getToStop() == null){
                                r.toStop=busStop;
                            }
                            else{
                                BusRoute newRoute = new BusRoute(r);
                                newRoute.setToStop(busStop);
                                inDepthRoutes.add(newRoute);
                            }
                        }
                    }

                    routes.addAll(inDepthRoutes);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return routes;
    }

    public BusRoute getReverse(){
        BusRoute reverse = new BusRoute(this);
        BusStop from = reverse.getFromStop();
        reverse.setFromStop(reverse.getToStop());
        reverse.setToStop(from);
        return reverse;
    }

    @Override
    public String toString() {
        return "BusRoute{" +
                "fromStop=" + fromStop +
                ", toStop=" + toStop +
                '}';
    }
}
