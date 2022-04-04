package com.company;

import java.util.*;

public class Path {
    private Map<BusLine, List<BusRoute>> paths;

    public Path(LinkedHashMap<BusLine, List<BusRoute>> paths) {
        this.paths = paths;
    }

    public Path() {
        this(new LinkedHashMap<BusLine, List<BusRoute>>());
    }

    public void addPath(BusLine l, List<BusRoute> r){
        this.paths.put(l, r);
    }

    public int length(){
        int result = 0;
        List<List<BusRoute>> r = new ArrayList<>(this.paths.values());
        for(int i=0; i<r.size(); i++){
            result += r.get(i).size();
        }
        return result;
    }

    @Override
    public String toString() {
        String result = "path : \n";

        for(Map.Entry<BusLine, List<BusRoute>> mapentry : this.paths.entrySet()){
            result += mapentry.getKey().toString() + "\n";
            for(BusRoute r : mapentry.getValue()){
                result += "- " + r + "\n";
            }
        }

        return result;
    }
}
