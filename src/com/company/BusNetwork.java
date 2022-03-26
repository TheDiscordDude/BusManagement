package com.company;

import java.util.ArrayList;
import java.util.List;

public class BusNetwork {
    private List<BusLine> busLines;

    public BusNetwork(List<BusLine> busLines){
        this.busLines = busLines;
    }

    public BusNetwork(){
        this(new ArrayList<BusLine>());
    }

    public void addBusLine(String name, String filePath){
        BusLine line = new BusLine(name, filePath);
        this.busLines.add(line);
    }

    public void addBusLine(BusLine line){
        this.busLines.add(line);
    }

    public List<BusRoute> getShortestPathBetween(BusStop from, BusStop to ){
        List<BusLine> availableDepartureLines = new ArrayList<>();
        List<BusLine> availableArrivalLines = new ArrayList<>();

        for(BusLine l : this.busLines){
            if(l.contains(from)){
                availableDepartureLines.add(l);
            }
            if(l.contains(to)){
                availableArrivalLines.add(l);
            }
        }

        List<BusLine> completeLines = new ArrayList<>();
        for(BusLine l : availableDepartureLines){
            if(availableArrivalLines.contains(l)){
                completeLines.add(l);
            }
        }

        if(completeLines.size() > 0){
            System.out.println(completeLines.size());
            for(BusLine l: completeLines){
                l.getShortestPathBetween(from, to);
            }
        }

        return null;

    }
    public List<BusRoute> getFastestPathBetween(BusStop from, BusStop to ){
        return null;
    }

    public boolean contains(BusStop busStop){
        for(BusLine line : this.busLines){
            if(line.contains(busStop))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "BusNetwork{";
        for(BusLine line : this.busLines){
            result += line.toString();
        }
        result+="}";
        return result;
    }

}
