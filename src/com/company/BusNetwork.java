package com.company;

import java.util.*;

public class BusNetwork {
    private final List<BusStop> busStops;
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
     * Returns the list of Bus route you need to take to get to your destination.
     * This method utilises the Dijkstra algorithm.
     * @param start     Our point of origin
     * @param finish    The destination we wish to reach
     * @param method    the method we wish to use : FASTEST path, SHORTEST path of FARMOST
     * @return The list of Bus route you need to take to get to your destination
     */
    public List<Route> getPathBetween(BusStop start, BusStop finish, Date departureTime, Method method) {
        // We initialise the process
        int size = this.busStops.size();

        // Set the weight table to infinite values
        HashMap<BusStop, Double> weights = new HashMap<>(size);
        for(BusStop b: this.busStops){
            weights.put(b, Double.POSITIVE_INFINITY);
        }
        // we set the weight of the first node to 0
        weights.put(start, 0.0);

        // we keep track of the used nodes
        List<BusStop> usedNodes = new ArrayList<>();

        // we also need to keep track of the predecessor of a node
        HashMap<BusStop, Route> predecessorTable = new HashMap<>(size);
        for(BusStop b: this.busStops){
            predecessorTable.put(b, null);
        }

        // the first node to be treated is the starting point
        BusStop currentNode = start;

        List<Route> toBeTreated;

        Date predecessorArrivalTime = departureTime;
        do {
            toBeTreated = new ArrayList<>(findAllRoutesFrom(currentNode));

            for (Route r : toBeTreated){
                // In this segment, we need to find when we will be arriving to the current node,
                // so we can calculate the time needed to get to the next stop
                Route predecessorRoute;
                predecessorRoute = predecessorTable.get(currentNode);
                if(predecessorRoute != null)
                    predecessorArrivalTime = predecessorRoute.getArrivalTime();

                double weight;
                // This is where things are different for each and every Method. The weight is calculated differently
                switch (method){
                    case SHORTEST -> weight = r.getWeight(weights.get(currentNode));
                    case FASTEST -> weight = r.getWeight(weights.get(currentNode), predecessorArrivalTime);
                    case FARMOST -> weight = r.getWeight(weights.get(currentNode), predecessorArrivalTime, predecessorTable);
                    default -> weight=Double.POSITIVE_INFINITY;
                }

                // If we find a weight that is lower than the current weight of the node, ...
                if(weights.get(r.getDestination()) > weight ){
                    // ... we update the weight and ...
                    weights.put(r.getDestination(), weight);
                    // ... we update the predecessor table
                    predecessorTable.put(r.getDestination(), r);
                    r.setChosenSchedule(predecessorArrivalTime);
                }
            }
            usedNodes.add(currentNode);

            // In this section, we choose the node that will be used next.
            double minValue = 9999;
            BusStop electedNode = null;
            for(BusStop bs : this.busStops){
                if(!usedNodes.contains(bs) && weights.get(bs) != Double.POSITIVE_INFINITY){
                    if(minValue > weights.get(bs)){
                        minValue = weights.get(bs);
                        electedNode = bs;
                    }
                }
            }
            currentNode = electedNode;

        }while ( usedNodes.size() < this.busStops.size());
        return getFinalChain(start, finish, predecessorTable);
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