package com.company;

import java.util.*;

public class BusNetwork {
    private List<BusStop> busStops;
    private List<Route> routes;

    public BusNetwork(List<BusStop> busStops) {
        this.busStops = busStops;
        this.routes = new ArrayList<>();
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public BusStop findBusStop(String name){
        BusStop result = null;
        for(BusStop b : this.busStops){
            if(b.getName().equals(name)){
                result = b;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the list of Bus route you need to take to get to your destination
     * @param start     Our point of origin
     * @param finish    The destination we wish to reach
     * @param method    the method we wish to use : FASTEST path, SHORTEST path of FARMOST
     * @return The list of Bus route you need to take to get to your destination
     */
    public List<Route> getPathBetween(BusStop start, BusStop finish, Date departureTime, Method method) {

        int nb = this.busStops.size();

        // initialisation

        double[] weights = new double[nb];
        Arrays.fill(weights, 999999999);

        List<Integer> usedNodesId = new ArrayList<>();

        Route[] predecessorTable = new Route[nb];
        Arrays.fill(predecessorTable, null);

        int[] shceduleId = new int[nb];
        Arrays.fill(shceduleId, -1);

        int currentNodeId = getBusStopId(start);

        // the first routes that need to be treated are those coming
        List<Route> toBeTreated;

        // to finish the initialization, we set the weight of the first node to 0
        weights[currentNodeId] = 0;
        Date predecessorArrivalTime = departureTime;
        do {
            toBeTreated = new ArrayList<>(findAllRoutesFrom(this.busStops.get(currentNodeId)));

            for (Route r : toBeTreated){
                // choper la date d'arriver de son predecesseur afin de :
                Route predecessorRoute = predecessorTable[currentNodeId];
                if(predecessorRoute != null)
                    predecessorArrivalTime = predecessorRoute.getArrivalTime();

                int id = getBusStopId(r.getDestination());
                double weight;

                switch (method){
                    case SHORTEST -> weight = r.getWeight(weights[currentNodeId]);
                    case FASTEST -> weight = r.getWeight(weights[currentNodeId], predecessorArrivalTime);
                    default -> weight=999999999;
                }
                if(weights[id] > weight ){
                    weights[id] = weight;
                    predecessorTable[id] = r;
                    r.setChosenSchedule(predecessorArrivalTime);
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
        }while (usedNodesId.size() < this.busStops.size() && currentNodeId != 9999);

        return getFinalChain(start, finish, predecessorTable);
    }


    /**
     * This method returns the id of the bus Stop in the "busStops" array
     * @param busStop the bus stop you wish to get the id from
     * @return an int : -1 if the bus stop has not been found and a regular int if it has been found
     */
    private int getBusStopId(BusStop busStop ){
        for(int i = 0; i < this.busStops.size(); i++){
            if(this.busStops.get(i).equals(busStop )){
                return i;
            }
        }
        return -1;
    }

    /** Finds all the routes going from the bus stop passed in parameter
     * @param startingPoint the starting point
     * @return a list of routes going from the starting point to other stops
     */
    public List<Route> findAllRoutesFrom(BusStop startingPoint){
        List<Route> result = new ArrayList<>();
        for(Route r : this.routes){
            if(r.getStartingPoint().equals(startingPoint)){
                result.add(r);
            }
        }
        return result;
    }


    /** Constructs the final chain of routes from the predecessor table from the dijkstra algorithm
     * @param startingPoint The starting point.
     * @param destination The bus stop where we want to go
     * @param predecessorTable the predecessor table generated from the dijkstra algorithm
     * @return a List of routes describing how to get to our destination from the starting point
     */
    private List<Route> getFinalChain(BusStop startingPoint, BusStop destination, Route[] predecessorTable){
        List<Route> finalChain = new ArrayList<>();

        int startingId = getBusStopId(startingPoint);
        int currentNodeId = getBusStopId(destination);

        while (currentNodeId != startingId){
            finalChain.add(predecessorTable[currentNodeId]);
            currentNodeId = getBusStopId(predecessorTable[currentNodeId].getStartingPoint());
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