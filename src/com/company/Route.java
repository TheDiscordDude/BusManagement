package com.company;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.*;

public class Route {
    private BusStop startingPoint;
    private BusStop destination;
    private String busLine;
    private double travelTime;

    public Route(BusStop fromStop, BusStop toStop, String busLine, Double travelTime) {
        this.startingPoint = fromStop;
        this.destination = toStop;
        this.busLine = busLine;
        this.travelTime = travelTime;
    }

    public Route(Route otherRoute){
        this(otherRoute.getStartingPoint(), otherRoute.getDestination(), otherRoute.getBusLine(), otherRoute.getTravelTime());
    }

    public Route(){
        this(null, null, null, 0.0);
    }

    public BusStop getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(BusStop startingPoint) {
        this.startingPoint = startingPoint;
    }

    public BusStop getDestination() {
        return destination;
    }

    public void setDestination(BusStop destination) {
        this.destination = destination;
    }

    public String getBusLine() {
        return busLine;
    }

    public void setBusLine(String busLine) {
        this.busLine = busLine;
    }

    public void setTravelTime(double travelTime) {
        this.travelTime = travelTime;
    }

    public double getTravelTime() {
        return travelTime;
    }

    public Double getWeight(double predecessorWeight){
        return predecessorWeight+1;
    }


    /** calculates the weight of an edge
     * @param predecessorWeight the weight of the predecessor node
     * @param arrivalTime the time of arrival
     * @return the weight as a Double
     */
    public Double getWeight(double predecessorWeight, Instant arrivalTime){
        // todo : use the arrival time to get the real weight
        return predecessorWeight+this.getTravelTime();
    }


    /** Reverses the Path :
     * when the current route goes from A to B,
     * the returned value will be a route going from B to A
     * @return the reverse Route
     */
    public Route getReverse(){
        Route reverse = new Route(this);
        BusStop from = reverse.getStartingPoint();
        reverse.setStartingPoint(reverse.getDestination());
        reverse.setDestination(from);
        return reverse;
    }

    @Override
    public String toString() {
        return startingPoint + " -> " + destination + "("+this.busLine+")";
        //return startingPoint + " " + destination + " " + this.travelTime;
    }

    /** The method looks into the files to calculate the travel time between 2 bus stops
     * @param busLine the bus line we need to use
     * @param starting the starting point
     * @param destination the destination
     * @return a double representing the number of minutes passed between the 2 bus stops
     */
    public static double computeTravelTime(String busLine, BusStop starting, BusStop destination) {
        String filePath = "";
        if (busLine.equals("sibra1"))
            filePath="1_Poisy-ParcDesGlaisins.txt";
        else if (busLine.equals("sibra2"))
            filePath="2_Piscine-Patinoire_Campus.txt";
        // we read the file
        String content = null;
        try{
            content = Files.readString(Paths.get(filePath));
        } catch (IOException e){
            e.printStackTrace();
        }

        Pattern pattern1 = Pattern.compile(starting+" (-|[0-9]).*\\r\\n" +destination+" (-|[0-9]).*", Pattern.MULTILINE);
        Pattern pattern2 = Pattern.compile(starting+" (-|[0-9]).*\\r\\n.*\\r\\n" +destination+" (-|[0-9]).*", Pattern.MULTILINE);
        Matcher matcher1 = pattern1.matcher(content);
        Matcher matcher2 = pattern2.matcher(content);

        String match = null;
        String line1 = null;
        String line2 = null;

        if(matcher1.find()){
            match = matcher1.group(0);
            line1 = match.split("\n")[0];
            line2 = match.split("\n")[1];
        } else if(matcher2.find()){
            match = matcher2.group(0);
            line1 = match.split("\n")[0];
            line2 = match.split("\n")[2];
        }

        String[] timetable1 = line1.split(" ");
        String[] timetable2 = line2.split(" ");

        Pattern timePattern = Pattern.compile("[0-9]*:[0-9]*");
        for(int i = 0; i < timetable1.length; i++){
            Matcher m1 = timePattern.matcher(timetable1[i]);
            Matcher m2 = timePattern.matcher(timetable2[i]);

            if(m1.find() && m2.find()){
                try{
                    Date d1 = new SimpleDateFormat("HH:mm").parse(timetable1[i]);
                    Date d2 = new SimpleDateFormat("HH:mm").parse(timetable2[i]);
                    Duration d = Duration.between(d1.toInstant(), d2.toInstant());
                    return Double.valueOf(d.getSeconds()/60);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /** Loads every bus routes from the files.
     * @param busStops
     * @return
     */
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
                        currentRoute.setStartingPoint(busStop);

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
                            if(r.getDestination() == null){
                                r.setDestination(busStop);
                            }
                            else{
                                Route newRoute = new Route(r);
                                newRoute.setDestination(busStop);
                                inDepthRoutes.add(newRoute);
                            }
                        }
                    }
                    routes.addAll(inDepthRoutes);
                }
                for(Route r : routes){

                    if(r.getBusLine() == null){
                        r.setBusLine("sibra" + (filePath.split("_"))[0]);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Route> reverseRoutes = new ArrayList<>();
        for(Route r: routes) {
            Route reverseRoute = r.getReverse();
            reverseRoutes.add(reverseRoute);
        }
        routes.addAll(reverseRoutes);
        for(Route r: routes){
            Double travelTime = computeTravelTime(r.getBusLine(), r.getStartingPoint(), r.getDestination());
            r.setTravelTime(travelTime);
        }
        return routes;
    }
}
