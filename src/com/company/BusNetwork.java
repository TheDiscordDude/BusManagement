package com.company;

import java.util.*;

public class BusNetwork {
    private List<BusStop> busStops;
    private List<Route> routes;

    public BusNetwork(List<BusStop> busStops, List<Route> routes) {
        this.busStops = busStops;
        this.routes = routes;
    }

    public List<Route> getPathBetween(BusStop start, BusStop finish, Method method) {
        int nb = this.busStops.size();

        double[] weights = new double[nb];
        Arrays.fill(weights, 99999999);

        List<Integer> usedNodesId = new ArrayList<>();

        Route[] predecessorTable = new Route[nb];
        Arrays.fill(predecessorTable, null);

        int currentNodeId = getBusStopId(start);

        List<Route> toBeTreated = findAllRoutesFrom(this.busStops.get(currentNodeId));

        weights[currentNodeId] = 0;
        do {

            for (Route r : toBeTreated){
                int id = getBusStopId(r.getToStop());
                double weight = r.getWeight(weights[currentNodeId]);
                if(weights[id] > weight ){
                    weights[id] = weight;
                    predecessorTable[id] = r;
                }
            }
            usedNodesId.add(currentNodeId);

            double minValue = 9999;
            int electedNodeId = 9999;

            for(int i =0; i < weights.length ;i ++){
                if(!usedNodesId.contains(i) && weights[i] != 99999999 ){
                    if(minValue > weights[i]){
                        minValue = weights[i];
                        electedNodeId = i;
                    }
                }
            }
            if (electedNodeId == 9999){
                break;
            }
            toBeTreated = new ArrayList<>(findAllRoutesFrom(this.busStops.get(electedNodeId)));
            currentNodeId = electedNodeId;

        }while (usedNodesId.size() < this.busStops.size());

        return getFinalChain(start, finish, predecessorTable);
    }

    public int getBusStopId(BusStop busStop ){
        for(int i = 0; i < this.busStops.size(); i++){
            if(this.busStops.get(i).equals(busStop )){
                return i;
            }
        }
        return -1;
    }

    public List<Route> findAllRoutesFrom(BusStop from){
        List<Route> result = new ArrayList<>();
        for(Route r : this.routes){
            if(r.getFromStop().equals(from)){
                result.add(r);
            }
        }
        return result;
    }

    private List<Route> getFinalChain(BusStop start, BusStop finish, Route[] predecessorTable){
        List<Route> finalChain = new ArrayList<>();

        int startingId = getBusStopId(start);
        int currentNodeId = getBusStopId(finish);

        while (currentNodeId != startingId){
            finalChain.add(predecessorTable[currentNodeId]);
            currentNodeId = getBusStopId(predecessorTable[currentNodeId].getFromStop());
        }
        Collections.reverse(finalChain);
        return finalChain;
    }

    @Override
    public String toString() {
        return "BusNetwork{" +
                "busStops=" + busStops.size() +
                ", routes=" + routes.size() +
                '}';
    }
}