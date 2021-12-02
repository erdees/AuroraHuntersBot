package ru.aurorahunters.bot.service.solarwind.ace;

import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.model.solardata.EpamSat;
import ru.aurorahunters.bot.model.solardata.MagnetSat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class ACEFetcher {

    private static final int NOMINAL_DATA = 0;

    public Map<String, ArrayList<String>> getLatestData() throws IOException {
        LinkedHashMap<String, ArrayList<String>> map;
        List<EpamSat> epamSatList = parseEpam(getFromUrl(Config.getAceEpam()));
        List<MagnetSat> magnetSatList = parseMagnet(getFromUrl(Config.getAceMagnet()));
        map = mergeSources(epamSatList, magnetSatList);

        return map;
    }

    private String getFromUrl(String url) throws IOException {
        URL magnetUrl =  new URL(url);
        BufferedReader input = new BufferedReader(new InputStreamReader(magnetUrl.openStream()));
        String joinedString = input.lines().collect(Collectors.joining("\n"));
        input.close();
        return joinedString;
    }

    private List<EpamSat> parseEpam(String rawData) {
        List<EpamSat> epamSatList = new ArrayList<>();
        String temp = rawData.replaceAll("[#|:].*\n", "");
        String[] lines = temp.split(System.getProperty("line.separator"));

        for (String s : lines)
        {
            if (getSourceStatus(s) == NOMINAL_DATA)
            {
                try {
                    epamSatList.add(parseEpamLine(s));
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return epamSatList;
    }

    private List<MagnetSat> parseMagnet(String rawData) {
        List<MagnetSat> magnetSatList = new ArrayList<>();
        String temp = rawData.replaceAll("[#|:].*\n", "");
        String[] lines = temp.split(System.getProperty("line.separator"));

        for (String s : lines)
        {
            if (getSourceStatus(s) == NOMINAL_DATA)
            {
                try {
                    magnetSatList.add(parseMagnetLine(s));
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return magnetSatList;
    }

    private MagnetSat parseMagnetLine(String s) {
        String[] lineParts = s.trim().replaceAll(" +", " ").split(" ");
        String year = lineParts[0];
        String month = lineParts[1];
        String day = lineParts[2];
        String hour = lineParts[3].substring(0,2);
        String minute = lineParts[3].substring(2,4);
        String dateFormat = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00";
        Timestamp timestamp = Timestamp.valueOf(dateFormat);

        double bz = Double.parseDouble(lineParts[9]);
        double bt = Double.parseDouble(lineParts[10]);

        return new MagnetSat(timestamp, bz, bt);
    }

    private EpamSat parseEpamLine(String s) {
        String[] lineParts = s.trim().replaceAll(" +", " ").split(" ");
        String year = lineParts[0];
        String month = lineParts[1];
        String day = lineParts[2];
        String hour = lineParts[3].substring(0,2);
        String minute = lineParts[3].substring(2,4);
        String dateFormat = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00";
        Timestamp timestamp = Timestamp.valueOf(dateFormat);

        double density = Double.parseDouble(lineParts[7].trim());
        double speed = Double.parseDouble(lineParts[8].trim());

        return new EpamSat(timestamp, density, speed);
    }

    private int getSourceStatus(String line) {
        int status = 0;
        String[] lineParts = line.trim().replaceAll(" +", " ").split(" ");
        status = Integer.parseInt(lineParts[6]);

        return status;
    }

    private LinkedHashMap<String, ArrayList<String>> mergeSources(List<EpamSat> epam,
                                                                   List<MagnetSat> mag) {
        LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<>();
        for (EpamSat e : epam)
        {
            String timeTagEpam = e.getTimeStamp().toString();
            String density = String.valueOf(e.getDensity());
            String speed = String.valueOf(e.getSpeed());
            for (MagnetSat m : mag)
            {
                String timeTagMag = String.valueOf(m.getTimeStamp());
                String bzGsm = String.valueOf(m.getBzGsm());
                String bt = String.valueOf(m.getBtGsm());
                if (timeTagMag.equals(timeTagEpam))
                {
                    map.put(timeTagEpam, new ArrayList<>(Arrays.asList(density, speed, bzGsm, bt)));
                }
            }
        }
        return map;
    }
}
