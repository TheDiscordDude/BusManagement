package com.company;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Route {
    private BusStop fromStop;
    private BusStop toStop;
    private Instant departureTime;
    private Instant arrivalTime;
    private String busLine;

    public Route(BusStop fromStop, BusStop toStop, Instant departureTime, Instant arrivalTime, String busLine) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.busLine = busLine;
    }

    public Route(Route otherRoute){
        this(otherRoute.getFromStop(), otherRoute.getToStop(), otherRoute.getDepartureTime(), otherRoute.getArrivalTime(), otherRoute.getBusLine());
    }

    public Route(){
        this(null, null, null, null, null);
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

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public Instant getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Instant arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getBusLine() {
        return busLine;
    }

    public void setBusLine(String busLine) {
        this.busLine = busLine;
    }

    public Duration getWeight(){
        return Duration.between(this.arrivalTime,this.departureTime);
    }

    public static List<Route> load(List<BusStop> busStops){
        List<String> filePaths = new ArrayList<String>();
        filePaths.add("1_Poisy-ParcDesGlaisins.txt");
        filePaths.add("2_Piscine-Patinoire_Campus.txt");
        List<Route> routes = new ArrayList<Route>();

        for (String filePath : filePaths) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
                String firstLine = lines.get(0);
                String[] busStopsNames = firstLine.split(" N ");
                for (int i = 0; i < busStopsNames.length-1 ;i ++) {
                    List<Route> inDepthRoutes = new ArrayList<Route>();
                    String[] departures = busStopsNames[i].split(" \\+ ");
                    String[] arrivals = busStopsNames[i+1].split(" \\+ ");
                    for(String departure : departures){
                        BusStop busStop = null;
                        for(BusStop b : busStops){
                            if (Objects.equals(b.getName(), departure)){
                                busStop = b;
                            }
                        }
                        Route currentRoute = new Route();
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
                        for(Route r : inDepthRoutes){
                            if(r.getToStop() == null){
                                r.toStop=busStop;
                            }
                            else{
                                Route newRoute = new Route(r);
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

    @Override
    public String toString() {
        return "Route{" +
                "fromStop=" + fromStop +
                ", toStop=" + toStop +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", busLine='" + busLine + '\'' +
                '}';
    }
}
