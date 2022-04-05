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

    public Path getPathBetween(BusStop from, BusStop to, Method method){
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
        List<Path> paths = new ArrayList<Path>();

        // We check the shortest path for each complete line
        if(completeLines.size() > 0){
            for(BusLine l: completeLines){
                Path p = new Path();
                switch (method){
                    case SHORTEST :
                        p.addPath(l, l.getShortestPathBetween(from, to));
                    case FASTEST :
                        break;
                    default :
                        break;

                }
                paths.add(p);
            }
        }

        // we then need to check the shortest path for routes with different BusLines :
        for(List<BusLine> ls : allPossibilities){
            BusStop commonStop = BusLine.commonStop(ls.get(0), ls.get(1));
            Path p = new Path();
            switch (method){
                case SHORTEST :
                    p.addPath(ls.get(0), ls.get(0).getShortestPathBetween(from, commonStop));
                    p.addPath(ls.get(1), ls.get(1).getShortestPathBetween(commonStop, to));

                case FASTEST:
                    break;

                default:
                    break;
            }

            paths.add(p);
        }

        Path result = paths.get(0);
        int i = 0;
        do {
            if(result.length() > paths.get(i).length()){
                result = paths.get(i);
            }
            i++;
        }while (i < paths.size());

        return result;
    }

    public BusStop findBusStop(String name){
        BusStop b = new BusStop(name);
        for(BusLine l : this.busLines){
            for(BusStop b1 : l.getBusStops()){
                if(b1.equals(b)){
                    return b1;
                }
            }
        }
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
