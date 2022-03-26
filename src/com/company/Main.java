package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        BusStop s = new BusStop("PARC_DES_GLAISINS");
        BusLine sibra1 = new BusLine("sibra1", "1_Poisy-ParcDesGlaisins.txt");
        BusLine sibra2 = new BusLine("sibra2", "2_Piscine-Patinoire_Campus.txt");
        List<BusLine> busLines = new ArrayList<BusLine>();

        busLines.add(sibra1);
        busLines.add(sibra2);
        BusNetwork network = new BusNetwork(busLines);
        System.out.println(network);
        //System.out.println(network.contains(s));

        BusStop b1 = sibra1.getBusStops().get(2);
        BusStop b2 = sibra1.getBusStops().get(5);

        System.out.println(b1);
        System.out.println(b2);

        network.getShortestPathBetween(b1, b2);
    }
}
