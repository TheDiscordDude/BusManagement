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

        List<List<BusLine>> allPossibilities = new ArrayList<>(); // cartesian product between l1 and l2
        List<BusLine> completeLines = new ArrayList<>();

        for(BusLine l1 : availableDepartureLines){
            for(BusLine l2 : availableArrivalLines){
                if(l1 == l2)
                    completeLines.add(l1);
                else{
                    List<BusLine> p = new ArrayList<>();
                    p.add(l1);
                    p.add(l2);
                    allPossibilities.add(p);
                }
            }
        }

        // All the paths are going in there
        List<List<BusRoute>> paths = new ArrayList<List<BusRoute>>();

        // We check the shortest path for each complete line
        if(completeLines.size() > 0){
            for(BusLine l: completeLines){
                paths.add(l.getShortestPathBetween(from, to));
            }
        }

        // we then need to check the shortest path for routes with different BusLines :
        for(List<BusLine> ls : allPossibilities){
            // todo:here my dude
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
