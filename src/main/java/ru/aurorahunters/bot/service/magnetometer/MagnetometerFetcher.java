package ru.aurorahunters.bot.service.magnetometer;

import ru.aurorahunters.bot.enums.MagnetEnum;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class MagnetometerFetcher {

    private final MagnetEnum type;

    /**
     * A constructor which receives a Enum data for particular magnetometer.
     * In classes and methods above this one, which can be called for different magnetometers.
     * @param type MagnetometerTypeEnum
     */
    public MagnetometerFetcher(MagnetEnum type) {
        this.type = type;
    }

    /**
     * Throughput method to request a raw magnetometer data from given URL (1 hour data)
     * This method can be use by multiple magnetometers which have the same source format.
     * @return contents a raw magnetometer data text with spaces and multiple lines.
     */
    public Map<Timestamp, ArrayList<Double>> getLatestData() throws IOException {
        URL magnetUrl =  new URL(type.getLatestDataUrl());
        BufferedReader magnetIn = new BufferedReader(new InputStreamReader(magnetUrl.openStream()));
        String joinedString = magnetIn.lines().collect(Collectors.joining("\n"));
        magnetIn.close();
        return parseAndStructureMagnets(joinedString);
    }

    /**
     * Throughput method to request a raw magnetometer data from given URL (24 hours data)
     * This method can be use by multiple magnetometers which have the same source format.
     * @return contents a raw magnetometer data text with spaces and multiple lines.
     */
    public Map<Timestamp, ArrayList<Double>> getDailyData() throws IOException {
        URL magnetUrl =  new URL(type.getDailyDataUrl());
        BufferedReader magnetIn = new BufferedReader(new InputStreamReader(magnetUrl.openStream()));
        String joinedString = magnetIn.lines().collect(Collectors.joining("\n"));
        magnetIn.close();
        return parseAndStructureMagnets(joinedString);
    }

    /**
     * Method creates a "Main" HashMap with magnetometer data which should be requested when database
     * need to have an updated magnetometer data. Method will process each line of a String and send it to
     * parseSingleMagnetLine() method for parse each line separately.
     * @param magnetString contents a raw magnetometer data text with spaces and multiple lines.
     * @return a database-ready format HashMap
     */
    private Map<Timestamp,ArrayList<Double>> parseAndStructureMagnets(String magnetString) {
        TreeMap<Timestamp,ArrayList<Double>> map = new TreeMap<>();
        Scanner s = new Scanner(magnetString);
        int i = 0;
        while (s.hasNextLine()) {
            if (i < 2) {
                s.nextLine();
            } else {
                Map.Entry<Timestamp, ArrayList<Double>> entry =
                        parseSingleLine(s.nextLine()).entrySet().iterator().next();
                map.put(entry.getKey(), entry.getValue());
            }
            i++;
        }
        s.close();
        return map;
    }

    /**
     * A method which parses a single line with magnetometer data to a HashMap.
     * @param s contents a raw text with spaces and tabs which will be parsed in a method.
     * @return a database ready format HashMap, which contents a Timestamp as a key and ArrayList
     * with x, y, and z values of a magnetometer. This HashMap will be used while a main HashMap
     * with being formed.
     */
    private Map<Timestamp,ArrayList<Double>> parseSingleLine(String s) {
        TreeMap<Timestamp,ArrayList<Double>> map = new TreeMap<>();
        ArrayList<Double> list = new ArrayList<>();
        String temp = s.replaceAll("\\s+", " ");
        String[] lineParts = temp.split(" ");
        String year = lineParts[0];
        String month = lineParts[1];
        String day = lineParts[2];
        String hour = lineParts[3];
        String minute = lineParts[4];
        String second = lineParts[5];
        double x = Double.parseDouble(lineParts[6]);
        double y = Double.parseDouble(lineParts[7]);
        double z = Double.parseDouble(lineParts[8]);
        String dateFormat = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        Timestamp outputFormat = Timestamp.valueOf(dateFormat);
        list.add(x);
        list.add(y);
        list.add(z);
        map.put(outputFormat,list);
        return map;
    }
}

