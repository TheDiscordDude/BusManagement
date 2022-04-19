package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

    /** This method loads all the bus stops from the files
     * @return a complete list of bus stops
     */
    public static List<BusStop> load() {

        // At first, we list the files with the bus schedules
        List<String> filePaths = new ArrayList<>();

        try {
            File dir = new File(".").getCanonicalFile();
            File[] files = dir.listFiles();
            for(File f : files){
                if(f.getName().endsWith(".txt")){
                    filePaths.add(f.getName());
                }
            }
        } catch (IOException e){
            System.out.println("IOException. Can't list files in directory");
            e.printStackTrace();
            System.exit(7);
        }


        List<BusStop> busStops = new ArrayList<>();

        for (String filePath : filePaths) {
            try {

                List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
                String firstLine = lines.get(0);
                String[] busStopsNames = firstLine.split(" N ");
                for (String name : busStopsNames) {
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

            } catch (IOException e) {
                System.out.println("IOException. File " + filePath + " not found while loading bus stops");
                e.printStackTrace();
                System.exit(6);
            }
        }

        return busStops;
    }

    @Override
    public String toString() {
        return  name;
    }
}
