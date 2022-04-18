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

        // We initialise the process

        int size = this.busStops.size();

        HashMap<BusStop, Double> weights = new HashMap<BusStop, Double>(size);
        for(BusStop b: this.busStops){
            weights.put(b, 999999999.9);
        }

        List<BusStop> usedNodes = new ArrayList<>();

        HashMap<BusStop, Route> predecessorTable = new HashMap<BusStop, Route>(size);
        for(BusStop b: this.busStops){
            predecessorTable.put(b, null);
        }

        BusStop currentNode = start;

        // the first routes that need to be treated are those coming
        List<Route> toBeTreated;

        // to finish the initialization, we set the weight of the first node to 0
        weights.put(currentNode, 0.0);
        Date predecessorArrivalTime = departureTime;
        do {
            toBeTreated = new ArrayList<>(findAllRoutesFrom(currentNode));

            for (Route r : toBeTreated){
                Route predecessorRoute;
                predecessorRoute = predecessorTable.get(currentNode);
                if(predecessorRoute != null)
                    predecessorArrivalTime = predecessorRoute.getArrivalTime();

                double weight;

                switch (method){
                    case SHORTEST -> {
                        weight = r.getWeight(weights.get(currentNode));
                    }
                    case FASTEST -> {
                        weight = r.getWeight(weights.get(currentNode), predecessorArrivalTime);
                    }
                    case FARMOST -> {
                        weight = r.getWeight(weights.get(currentNode), predecessorArrivalTime, predecessorTable);
                    }
                    default -> weight=999999999.9;
                }

                if(weights.get(r.getDestination()) > weight ){
                    weights.put(r.getDestination(), weight);
                    predecessorTable.put(r.getDestination(), r);
                    r.setChosenSchedule(predecessorArrivalTime);
                }
            }
            usedNodes.add(currentNode);

            double minValue = 9999;
            BusStop electedNode = null;

            for(BusStop bs : this.busStops){
                if(!usedNodes.contains(bs) && weights.get(bs) != 999999999.9){
                    if(minValue > weights.get(bs)){
                        minValue = weights.get(bs);
                        electedNode = bs;
                    }
                    /*
                        todo: update the weight of the other nodes when a better path is found (verify if that's actually useful ... pretty sure it's not)
                     */
                }
            }


            currentNode = electedNode;
        }while (usedNodes.size() < this.busStops.size() && currentNode != null);
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
    private List<Route> getFinalChain(BusStop startingPoint, BusStop destination, HashMap<BusStop, Route> predecessorTable){
        List<Route> finalChain = new ArrayList<>();

        BusStop currentNode = destination;

        while (!currentNode.equals(startingPoint)){
            finalChain.add(predecessorTable.get(currentNode));
            currentNode = predecessorTable.get(currentNode).getStartingPoint();
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