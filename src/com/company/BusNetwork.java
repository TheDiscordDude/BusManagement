package com.company;

import java.util.*;

public class BusNetwork {
    private List<BusStop> busStops;
    private List<Route> routes;

    public BusNetwork(List<BusStop> busStops, List<Route> routes) {
        this.busStops = busStops;
        this.routes = routes;
    }


    /**
     * Returns the list of Bus route you need to take to get to your destination
     * @param start     Our point of origin
     * @param finish    The destination we wish to reach
     * @param method    the method we wish to use : FASTEST path, SHORTEST path of FARMOST
     * @return The list of Bus route you need to take to get to your destination
     */
    public List<Route> getPathBetween(BusStop start, BusStop finish, Method method) {

        int nb = this.busStops.size();

        // initialisation

        double[] weights = new double[nb];
        Arrays.fill(weights, 99999999);

        List<Integer> usedNodesId = new ArrayList<>();

        Route[] predecessorTable = new Route[nb];
        Arrays.fill(predecessorTable, null);

        int currentNodeId = getBusStopId(start);

        // the first routes that need to be treated are those coming
        List<Route> toBeTreated = findAllRoutesFrom(this.busStops.get(currentNodeId));

        // to finish the initialization, we set the weight of the first node to 0
        weights[currentNodeId] = 0;
        do {
            toBeTreated = new ArrayList<>(findAllRoutesFrom(this.busStops.get(currentNodeId)));
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
                    /*
                        todo: update the weight of the other nodes when a better path is found (verify if that's actually useful ... pretty sure it's not)
                     */
                }
            }

            currentNodeId = electedNodeId;

        }while (usedNodesId.size() < this.busStops.size());

        return getFinalChain(start, finish, predecessorTable);
    }

    private int getBusStopId(BusStop busStop ){
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