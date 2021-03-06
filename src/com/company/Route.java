package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class Route {
    private BusStop startingPoint;
    private BusStop destination;
    private String busLine;
    private final List<Date> departureTimes;
    private final List<Date> arrivalTimes;
    private int chosenSchedule;

    public Route(BusStop fromStop, BusStop toStop, String busLine) {
        this.startingPoint = fromStop;
        this.destination = toStop;
        this.busLine = busLine;
        this.departureTimes = new ArrayList<>();
        this.arrivalTimes = new ArrayList<>();
        chosenSchedule= -1;
    }

    public Route(Route otherRoute){
        this(otherRoute.getStartingPoint(), otherRoute.getDestination(), otherRoute.getBusLine());
    }

    public Route(){
        this(null, null, null);
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

    public List<Date> getDepartureTimes() {
        return departureTimes;
    }

    public List<Date> getArrivalTimes() {
        return arrivalTimes;
    }

    private void addDepartureTime(Date t){
        this.departureTimes.add(t);
        Collections.sort(this.departureTimes);
    }

    private void addArrivalTime(Date t){
        this.arrivalTimes.add(t);
        Collections.sort(this.arrivalTimes);
    }

    public void setChosenSchedule(Date date) {
        this.chosenSchedule = -1;
        for(int i = 0; i < this.departureTimes.size(); i++){
            if(this.departureTimes.get(i).after(date) || this.departureTimes.get(i).toString().equals(date.toString()) ) {
                this.chosenSchedule = i;
                break;
            }
        }
    }

    /** Get the weight of this route. This specific definition is used for the SHORTEST method.
     * @param predecessorWeight the weight of the predecessor node
     * @return a double representing the weight of the route
     */
    public Double getWeight(double predecessorWeight){
        return predecessorWeight+1;
    }

    /** Get the weight of this route. This specific definition is used for the FASTEST method.
     * The weight here corresponds to the time-cost.
     * @param predecessorWeight the weight of the predecessor node
     * @param arrivalTime the time the passenger will arrive to the starting point of this route
     * @return a double representing the weight of the route
     */
    public Double getWeight(double predecessorWeight, Date arrivalTime){

        Date nextDepartureTime ;
        double travelDuration = 0.0;
        double waitingTime = 0.0;
        for(int i = 0; i < this.departureTimes.size(); i++){
            // we get the first scheduled bus right after the arrivalTime
            if(this.departureTimes.get(i).after(arrivalTime) || this.departureTimes.get(i).toString().equals(arrivalTime.toString()) ) {
                nextDepartureTime = this.departureTimes.get(i);
                Date nextArrivalTime = this.arrivalTimes.get(i);

                travelDuration = Duration.between(nextDepartureTime.toInstant(), nextArrivalTime.toInstant()).getSeconds()/60.0;
                waitingTime = Duration.between(arrivalTime.toInstant(), nextDepartureTime.toInstant()).getSeconds()/60.0;
                break;
            }
        }
        return predecessorWeight + waitingTime + travelDuration;
    }

    /** Get the weight of this route. This specific definition is used for the FARMOST method.
     * @param predecessorWeight the weight of the predecessor
     * @param arrivalTime the time the passenger will arrive to the starting point of this route
     * @param predecessorTable predecessor table calculated with the dijkstra algorithm
     * @return a double representing the weight of the route
     */
    public Double getWeight(double predecessorWeight, Date arrivalTime, HashMap<BusStop, Route> predecessorTable){
        // At first, we get the cost in time
        Double weight = this.getWeight(predecessorWeight, arrivalTime);

        // Then we calculate the depth of the node compared to the starting point
        List<Route> chain = new ArrayList<>();
        BusStop currentNode = this.startingPoint;
        // We know one thing : the starting point has no predecessor.
        while (!(predecessorTable.get(currentNode)==null)){
            chain.add(predecessorTable.get(currentNode));
            currentNode = predecessorTable.get(currentNode).getStartingPoint();
        }
        // Here we add the depth of the node to cost in time
        weight += chain.size();
        return weight;
    }

    public Date getArrivalTime(){
        return this.arrivalTimes.get(this.chosenSchedule);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "EEEE HH:mm").withZone(ZoneId.systemDefault());
        String departureTime = null;
        String arrivalTime = null;


        if(this.chosenSchedule > -1){
            departureTime = formatter.format(this.departureTimes.get(this.chosenSchedule).toInstant());
            arrivalTime = formatter.format(this.arrivalTimes.get(this.chosenSchedule).toInstant());
        }

        String result ;
        if(arrivalTime != null){
            result = startingPoint + "("+ departureTime +") -> " + destination + " (" + arrivalTime + ") - "+this.busLine;
        } else {
            result = startingPoint + " -> " + destination + " ("+ this.departureTimes.size() +")";
        }

        return result;
    }

    /** Calculates the whole schedule for a specific route
     * @param route the treated route
     * @param departureCalendar it corresponds to the departure time, just in Calendar format
     */
    public static void computeBusSchedules(Route route, Calendar departureCalendar){
        //
        Date departureTime = departureCalendar.getTime();
        boolean treatNextDay = false;
        BusStop starting = route.getStartingPoint();
        BusStop destination = route.getDestination();

        String filePath = "";

        String lineNumber = route.getBusLine().substring(route.getBusLine().length() -1);
        try {
            File dir = new File(".").getCanonicalFile();
            File[] files = dir.listFiles();
            for(File f : files){
                if(f.getName().startsWith(lineNumber) && f.getName().endsWith(".txt"))
                    filePath = f.getPath();
            }
        } catch (IOException e){
            System.out.println("IOException. Can't list files in directory");
            e.printStackTrace();
            System.exit(8);
        } catch (NullPointerException e){
            System.out.println("NullPointerException. No files have been found in this directory");
            e.printStackTrace();
            System.exit(10);
        }

        // we read the file
        String content = null;
        try{
            content = Files.readString(Paths.get(filePath));
        } catch (IOException e){
            System.out.println("IOException. File " + filePath + " not found while computing schedule for " + route);
            e.printStackTrace();
            System.exit(1);
        }
        String lineSeparator = System.getProperty("line.separator");
        // here there are 2 possibilities, either the 2 bus stops are on top of each other ...
        Pattern pattern1 = Pattern.compile(starting+" (-|[0-9]).*" + lineSeparator +destination+" (-|[0-9]).*", Pattern.MULTILINE);
        // or there is a gap between them
        Pattern pattern2 = Pattern.compile(starting+" (-|[0-9]).*"+lineSeparator+".*" + lineSeparator +destination+" (-|[0-9]).*", Pattern.MULTILINE);
        Matcher matcher1 = pattern1.matcher(content);
        Matcher matcher2 = pattern2.matcher(content);

        String match;
        String line1;
        String line2;
        List<String> matches = new ArrayList<>();
        while (matcher1.find()){
            matches.add(matcher1.group());
        }
        while (matcher2.find()){
            matches.add(matcher2.group());
        }
        if(departureCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                departureCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                departureCalendar.get(Calendar.MONTH) == Calendar.JULY ||
                departureCalendar.get(Calendar.MONTH) == Calendar.AUGUST)
        {
            // the date is in weekend or vacation,
            match = matches.get(1);
        }
        else {
            match = matches.get(0);
        }
        // the first line corresponds to the starting point of the route
        line1 = match.split("\n")[0];
        // the last line corresponds to the destination
        line2 = match.substring(match.lastIndexOf("\n"));

        String[] timetable1 = line1.split(" ");
        String[] timetable2 = line2.split(" ");

        Pattern timePattern = Pattern.compile("[0-9]*:[0-9]*");

        for(int i = 0; i < timetable1.length; i++){
            Matcher m1 = timePattern.matcher(timetable1[i]);
            Matcher m2 = timePattern.matcher(timetable2[i]);

            if(m1.find() && m2.find()){
                try{
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
                    Instant currentTime  = Instant.now();

                    String instantStr = formatter.format(currentTime);


                    Calendar c2 = Calendar.getInstance();
                    Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(instantStr + " " + timetable1[i]);
                    c2.setTime(d1);
                    c2.set(Calendar.MONTH, departureCalendar.get(Calendar.MONTH));
                    c2.set(Calendar.DATE, departureCalendar.get(Calendar.DATE));
                    c2.set(Calendar.YEAR, departureCalendar.get(Calendar.YEAR));
                    d1 = c2.getTime();

                    Date d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(instantStr + " " + timetable2[i]);
                    c2.setTime(d2);
                    c2.set(Calendar.MONTH, departureCalendar.get(Calendar.MONTH));
                    c2.set(Calendar.DATE, departureCalendar.get(Calendar.DATE));
                    c2.set(Calendar.YEAR, departureCalendar.get(Calendar.YEAR));
                    d2 = c2.getTime();

                    if(d1.after(departureTime) || d1.toString().equals(departureTime.toString())){
                        route.addDepartureTime(d1);
                        route.addArrivalTime(d2);
                    }
                    else{
                        treatNextDay = true;
                    }
                } catch (ParseException e) {
                    System.out.println("ParseException while computing schedule for : " + route);
                    e.printStackTrace();
                    System.exit(2);
                }
            }
        }

        // We always prefer to also compute day + 1 so there is always a way to access our destination
        if(treatNextDay){
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.set(Calendar.YEAR, departureCalendar.get(Calendar.YEAR));
            newCalendar.set(Calendar.MONTH, departureCalendar.get(Calendar.MONTH));
            newCalendar.set(Calendar.DAY_OF_YEAR, departureCalendar.get(Calendar.DAY_OF_YEAR) +1 );
            newCalendar.set(Calendar.HOUR_OF_DAY, 0);
            newCalendar.set(Calendar.MINUTE, 0);
            computeBusSchedules(route, newCalendar);
        }

    }

    /** Loads every bus routes from the files.
     * @param busStops All the busStops loaded
     * @param calendar the departure time in calendar format
     * @return the list of routes
     */
    public static List<Route> load(List<BusStop> busStops, Calendar calendar){
        List<String> filePaths = new ArrayList<>();

        // In this part, we get all the files that contain infos on bus lines
        try {
            File dir = new File(".").getCanonicalFile();
            File[] files = dir.listFiles();
            for(File f : files){
                if(f.getName().endsWith(".txt")){
                    filePaths.add(f.getName());
                }
            }
        } catch (IOException e){
            System.out.println("IOException. Can't list files in directory");
            e.printStackTrace();
            System.exit(9);
        }

        List<Route> routes = new ArrayList<>();

        // This whole segment read the routes from all the files in 1 direction.
        for (String filePath : filePaths) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
                String firstLine = lines.get(0);
                String[] busStopsNames = firstLine.split(" N ");
                for (int i = 0; i < busStopsNames.length-1 ;i ++) {
                    List<Route> inDepthRoutes = new ArrayList<>();
                    String[] departures = busStopsNames[i].split(" \\+ ");
                    String[] arrivals = busStopsNames[i+1].split(" \\+ ");
                    for(String departure : departures){
                        BusStop busStop = null;
                        for(BusStop b : busStops){
                            if (b.getName().equals(departure)){
                                busStop = b;
                                break;
                            }
                        }
                        Route currentRoute = new Route();
                        currentRoute.setStartingPoint(busStop);
                        currentRoute.setBusLine("sibra" + (filePath.split("_"))[0]);
                        inDepthRoutes.add(currentRoute);
                    }

                    for(String arrival : arrivals){
                        BusStop busStop = null;
                        for(BusStop b : busStops){
                            if (Objects.equals(b.getName(), arrival)){
                                busStop = b;
                                break;
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
            } catch (IOException e) {
                System.out.println("IOException while reading schedule files");
                e.printStackTrace();
                System.exit(3);
            }
        }

        // We got all the routes in 1 direction, we now need the same thing in the other direction
        List<Route> reverseRoutes = new ArrayList<>();
        for(Route r: routes) {
            Route reverseRoute = r.getReverse();
            reverseRoutes.add(reverseRoute);
        }
        routes.addAll(reverseRoutes);

        // Once we have all the necessary Routes setup, we calculate the schedules on each and every routes
        for(Route r: routes){
            computeBusSchedules(r, calendar);
        }
        return routes;
    }
}
