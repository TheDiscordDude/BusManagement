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
        return null;
    }
    public List<BusRoute> getFastestPathBetween(BusStop from, BusStop to ){
        return null;
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
