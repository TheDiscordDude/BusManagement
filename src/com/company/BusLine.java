package com.company;

import java.util.ArrayList;
import java.util.List;

public class BusLine {
    private final String name;
    private final List<BusStop> busStops;
    private final List<Direction> directions;

    public BusLine(String name, String pathToBusLine) {
        this.name = name;
        this.busStops = BusStop.load(pathToBusLine);
        List<BusRoute> busRoutesDirectionGo = BusRoute.load(this.busStops, pathToBusLine);
        List<BusRoute> busRoutesDirectionBack = new ArrayList<>();
        for (BusRoute route : busRoutesDirectionGo){
            busRoutesDirectionBack.add(route.getReverse());
        }

        this.directions = new ArrayList<>();

        Direction go = new Direction(this.busStops, busRoutesDirectionGo);
        Direction back = new Direction(this.busStops, busRoutesDirectionBack);

        this.directions.add(go);
        this.directions.add(back);
    }

    public String getName() {
        return name;
    }

    public List<BusStop> getBusStops() {
        return busStops;
    }

    public List<Direction> getDirections() {
        return directions;
    }

    @Override
    public String toString() {
        return "BusLine{" +
                "name='" + name + "' " +
                "busStops=" + this.busStops.size() + " " +
                "goDirection=" + this.directions.get(0).getLastBusStop().toString() + " " +
                "backDirection=" + this.directions.get(1).getLastBusStop().toString() +
                '}';
    }

    public boolean contains(BusStop busStop){
        return this.busStops.contains(busStop);
    }

    public static BusStop commonStop(BusLine line1, BusLine line2){
        for(BusStop b1 : line1.getBusStops()){
            if(line2.contains(b1))
                return b1;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        else {
            BusLine other = (BusLine) obj;
            return this.name == other.getName();
        }
    }

    public List<BusRoute> getShortestPathBetween(BusStop from, BusStop to){
        List<BusRoute> result = new ArrayList<>();

        for(Direction d :this.directions){
            if (d.pathExistsBetween(from, to)> -1 ){
                result = d.getShortestPathBetween(from, to);
            }
        }
        return result;
    }
}
