package com.company;

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

    public Double getWeight(double predecessorWeight){
        return predecessorWeight+1;
    }


    /** calculates the weight of an edge
     * @param predecessorWeight the weight of the predecessor node
     * @param arrivalTime the time of arrival
     * @return the weight as a Double
     */
    public Double getWeight(double predecessorWeight, Date arrivalTime){

        Date nextDepartureTime ;
        double travelDuration = 0.0;
        double waitingTime = 0.0;
        for(int i = 0; i < this.departureTimes.size(); i++){
            if(this.departureTimes.get(i).after(arrivalTime) || this.departureTimes.get(i).toString().equals(arrivalTime.toString()) ) {
                nextDepartureTime = this.departureTimes.get(i);
                Date nextArrivalTime = this.arrivalTimes.get(i);

                travelDuration = Duration.between(nextDepartureTime.toInstant(), nextArrivalTime.toInstant()).getSeconds()/60.0;
                waitingTime = Duration.between(arrivalTime.toInstant(), nextDepartureTime.toInstant()).getSeconds()/60.0;
                break;
            }
        }
        return predecessorWeight+ waitingTime + travelDuration;
    }

    public Double getWeight(double predecessorWeight, Date arrivalTime, HashMap<BusStop, Route> predecessorTable){
        Double weight = this.getWeight(predecessorWeight, arrivalTime);
        List<Route> chain = new ArrayList<>();

        BusStop currentNode = destination;

        while (!currentNode.equals(startingPoint) && !(predecessorTable.get(currentNode)==null)){
            chain.add(predecessorTable.get(currentNode));
            currentNode = predecessorTable.get(currentNode).getStartingPoint();
        }
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

    public static void computeBusSchedules(Route r, Calendar departureCalender){
        Date departureTime = departureCalender.getTime();

        String filePath = "";
        if (r.getBusLine().equals("sibra1"))
            filePath="1_Poisy-ParcDesGlaisins.txt";
        else if (r.getBusLine().equals("sibra2"))
            filePath="2_Piscine-Patinoire_Campus.txt";

        BusStop starting = r.getStartingPoint();
        BusStop destination = r.getDestination();
        // we read the file
        String content = null;
        try{
            content = Files.readString(Paths.get(filePath));
        } catch (IOException e){
            e.printStackTrace();

            System.exit(2);
        }

        Pattern pattern1 = Pattern.compile(starting+" (-|[0-9]).*\\r\\n" +destination+" (-|[0-9]).*", Pattern.MULTILINE);
        Pattern pattern2 = Pattern.compile(starting+" (-|[0-9]).*\\r\\n.*\\r\\n" +destination+" (-|[0-9]).*", Pattern.MULTILINE);
        Matcher matcher1 = pattern1.matcher(content);
        Matcher matcher2 = pattern2.matcher(content);

        String match;
        String line1 = null;
        String line2 = null;

        if(matcher1.find()){

            if(departureCalender.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || departureCalender.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
            {
                matcher1.find();
                match = matcher1.group(0);
            }
            else {
                match = matcher1.group(0);
            }

            line1 = match.split("\n")[0];
            line2 = match.split("\n")[1];
        } else if(matcher2.find()){

            if(departureCalender.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || departureCalender.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
            {
                matcher2.find();
                match = matcher2.group(0);
            }
            else {
                match = matcher2.group(0);
            }

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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
                    Instant currentTime  = Instant.now();

                    String instantStr = formatter.format(currentTime);


                    Calendar c2 = Calendar.getInstance();
                    Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(instantStr + " " + timetable1[i]);
                    c2.setTime(d1);
                    c2.set(Calendar.MONTH, departureCalender.get(Calendar.MONTH));
                    c2.set(Calendar.DATE, departureCalender.get(Calendar.DATE));
                    c2.set(Calendar.YEAR, departureCalender.get(Calendar.YEAR));
                    d1 = c2.getTime();

                    Date d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(instantStr + " " + timetable2[i]);
                    c2.setTime(d2);
                    c2.set(Calendar.MONTH, departureCalender.get(Calendar.MONTH));
                    c2.set(Calendar.DATE, departureCalender.get(Calendar.DATE));
                    c2.set(Calendar.YEAR, departureCalender.get(Calendar.YEAR));
                    d2 = c2.getTime();

                    if(d1.after(departureTime) || d1.toString().equals(departureTime.toString())){
                        r.addDepartureTime(d1);
                        r.addArrivalTime(d2);
                    }
                    else{
                        Calendar newCalendar = Calendar.getInstance();
                        newCalendar.set(Calendar.YEAR, departureCalender.get(Calendar.YEAR));
                        newCalendar.set(Calendar.MONTH, departureCalender.get(Calendar.MONTH));
                        newCalendar.set(Calendar.DAY_OF_YEAR, departureCalender.get(Calendar.DAY_OF_YEAR) +1 );
                        newCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        newCalendar.set(Calendar.MINUTE, 0);
                        computeBusSchedules(r, newCalendar);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /** Loads every bus routes from the files.
     * @param busStops All the busStops loaded
     * @return the list of routes
     */
    public static List<Route> load(List<BusStop> busStops, Calendar c){
        List<String> filePaths = new ArrayList<>();

        filePaths.add("1_Poisy-ParcDesGlaisins.txt");
        filePaths.add("2_Piscine-Patinoire_Campus.txt");

        List<Route> routes = new ArrayList<>();

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
            computeBusSchedules(r, c);
        }
        return routes;
    }
}
