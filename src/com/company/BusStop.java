package com.company;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.awt.Desktop;
import java.util.Objects;

public class BusStop {
    public String getName() {
        return name;
    }

    private final String name;

    public BusStop(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusStop busStop = (BusStop) o;
        return Objects.equals(name, busStop.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static List<BusStop> load() {
        List<String> filePaths = new ArrayList<String>();
        filePaths.add("1_Poisy-ParcDesGlaisins.txt");
        filePaths.add("2_Piscine-Patinoire_Campus.txt");
        List<BusStop> busStops = new ArrayList<BusStop>();

        for (String filePath : filePaths) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
                String firstLine = lines.get(0);
                String[] busStopsNames = firstLine.split(" N ");
                for (String name : busStopsNames) {
                    System.out.println(name + " -> ");
                    String[] temp = name.split(" \\+ ");
                    if (temp.length > 1) {
                        for (String s : temp) {
                            BusStop newBusStop = new BusStop(s);
                            if(!busStops.contains(newBusStop)){
                                busStops.add(newBusStop);
                            }
                        }
                    } else {
                        BusStop newBusStop = new BusStop(name);
                        if(!busStops.contains(newBusStop)){
                            busStops.add(newBusStop);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return busStops;
    }

    @Override
    public String toString() {
        return  name;
    }
}
