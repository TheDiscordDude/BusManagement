package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
            if(files == null)
                throw new NullPointerException();
            for(File f : files){
                if(f.getName().endsWith(".txt")){
                    filePaths.add(f.getName());
                }
            }
        } catch (IOException e){
            System.out.println("IOException. Can't list files in directory");
            e.printStackTrace();
            System.exit(7);
        } catch (NullPointerException e){
            System.out.println("NullPointerException. Can't list files in directory");
            e.printStackTrace();
            System.exit(13);
        }


        List<BusStop> busStops = new ArrayList<>();
        // Then, for all the files ...
        for (String filePath : filePaths) {
            try {

                List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
                String firstLine = lines.get(0);
                // ... we treat the first line we because that the one that contains all the infos.
                String[] busStopsNames = firstLine.split(" N ");
                for (String name : busStopsNames) {
                    String[] stops = name.split(" \\+ ");
                    if (stops.length > 1) {
                        for (String s : stops) {
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
