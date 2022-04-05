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
    private String busLine;

    public Route(BusStop fromStop, BusStop toStop, String busLine) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.busLine = busLine;
    }

    public Route(Route otherRoute){
        this(otherRoute.getFromStop(), otherRoute.getToStop(), otherRoute.getBusLine());
    }

    public Route(){
        this(null, null, null);
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

    public String getBusLine() {
        return busLine;
    }

    public void setBusLine(String busLine) {
        this.busLine = busLine;
    }

    public Double getWeight(double predecessorWeight){
        return predecessorWeight+1;
    }

    public Double getTimeWeight(double predecessorWeight, Instant arrivalDate){

        return null;
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
                List<Route> reverseRoutes = new ArrayList<>();
                for(int i=0; i< routes.size(); i ++){
                    if(routes.get(i).getBusLine() == null)
                        routes.get(i).setBusLine("sibra" + (filePath.split("_"))[0]);
                    reverseRoutes.add(routes.get(i).getReverse());
                    /*
                        todo: Faire en sorte que l'on trouve la ligne de bus dans la route.
                     */
                }
                routes.addAll(reverseRoutes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return routes;
    }

    public Route getReverse(){
        Route reverse = new Route(this);
        BusStop from = reverse.getFromStop();
        reverse.setFromStop(reverse.getToStop());
        reverse.setToStop(from);
        return reverse;
    }

    @Override
    public String toString() {
        return fromStop + " -> " + toStop + "("+this.busLine+")";
    }
}
