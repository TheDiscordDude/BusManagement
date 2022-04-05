package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        BusLine sibra1 = new BusLine("sibra1", "1_Poisy-ParcDesGlaisins.txt");
        BusLine sibra2 = new BusLine("sibra2", "2_Piscine-Patinoire_Campus.txt");
        List<BusLine> busLines = new ArrayList<BusLine>();

        busLines.add(sibra1);
        busLines.add(sibra2);

        BusNetwork network = new BusNetwork(busLines);
        System.out.println(network);

        BusStop b1 = network.findBusStop("France_Barattes");
        BusStop b2 = network.findBusStop("Parc_des_Sports");

        System.out.println(b1);
        System.out.println(b2);

        System.out.println(network.getPathBetween(b1, b2, Method.SHORTEST));
    }
}
