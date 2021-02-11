package ru.aurorahunters.bot.service.solarwind;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.dao.DataDAO;
import ru.aurorahunters.bot.model.solardata.FullSolarWindData;
import ru.aurorahunters.bot.model.solardata.MagData;
import ru.aurorahunters.bot.model.solardata.PlasmData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;
import java.util.*;

import static java.lang.System.*;

public class SunWindService implements Runnable {

    private String magnetJson;
    private String plasmaJson;
    private final int id;

    public SunWindService(int id) {
        this.id = id;
    }

    /**
     * Method which requests json files, merge and put them to the Database according to
     * ScheduledExecutorService settings. To have a relevant values, it is recommended to do it
     * at least every 1 minute. */
    public void run() {
        try {
            assignJsonURL();
            JsonArray magnetArray = new Gson().fromJson(this.magnetJson, JsonArray.class);
            JsonArray plasmaArray = new Gson().fromJson(this.plasmaJson, JsonArray.class);
            mergeJson(magnetArray, plasmaArray);
            insertResultsToDb(magnetArray, plasmaArray);
        } catch (Exception e) {
            out.println("Caught exception in ScheduledExecutorService. StackTrace:\n" + e);
        }
    }

    /**
     * Throughput method to make code readable. Will assign to magnetJson/plasmaJson fields
     * required Json URL for further fetching.
     */
    private void assignJsonURL() {
        try {
            getJsonsById(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Throughput method to make code readable. Call DAO method and translate Map to DB for
     * further insert.
     * @param magnetArray converted by gson magnetJson
     * @param plasmaArray converted by gson plasmaJson
     */
    private void insertResultsToDb(JsonArray magnetArray, JsonArray plasmaArray) {
        try {
            new DataDAO().insertResults(mergeJson(magnetArray, plasmaArray));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Choose required json pair according to its id.
     * @param id which is the id of json file pair.
     */
    private void getJsonsById(int id) throws IOException {
        // TODO get rid of "magic" numbers
        String tempMag = "";
        String tempPlasma = "";
        if (id == 1) {
            tempMag = Config.getMag5min();
            tempPlasma = Config.getPlasm5min();
        }
        if (id == 2) {
            tempMag = Config.getMag2h();
            tempPlasma = Config.getPlasm2h();
        }
        if (id == 3) {
            tempMag = Config.getMag24h();
            tempPlasma = Config.getPlasm24h();
        }
        if (id == 4) {
            tempMag = Config.getMag7day();
            tempPlasma = Config.getPlasm7day();
        }
        fetchJson(tempMag, tempPlasma);
    }

    /**
     * Fetch required json pair via URL.
     * @param mag mag json URL.
     * @param plasma plasma json URL.
     */
    public void fetchJson(String mag, String plasma) throws IOException {
        URL magUrl = new URL(mag);
        try (BufferedReader magIn = new BufferedReader(new InputStreamReader(magUrl.openStream()))) {
            magnetJson = magIn.readLine();
        }
        URL plasmaUrl = new URL(plasma);
        try (BufferedReader plasmaIn = new BufferedReader(new InputStreamReader(plasmaUrl.openStream()))) {
            plasmaJson = plasmaIn.readLine();
        }
    }

    /**
     * This method merges two different json arrays to a one single map where the map key is time_tag. Two different
     * fetched json files has time_tag relation. For example, when 'mag' and 'plasma' json has the same time_tag, their
     * parameters (bz_gsm, density, speed) should be placed to a map as a ArrayList with the time_tag map key.
     * @param mag JsonArray object of 'mag' json
     * @param plasm JsonArray object of 'plasma' json
     */
    private LinkedHashMap<Timestamp, FullSolarWindData> mergeJson(JsonArray mag, JsonArray plasm) {
        LinkedHashMap<Timestamp, FullSolarWindData> map = new LinkedHashMap<>();
        cleanIndex(mag, plasm);
        for (JsonElement plasmLine : plasm) {
            PlasmData plasmaField = getPlasmData(plasmLine);
            if (plasmaField == null) continue;
            for (JsonElement magLine : mag) {
                MagData magField = getMagData(magLine);
                if (magField == null) continue;
                if (plasmaField.getTimestamp().equals(magField.getTimestamp())) {
                        map.put(plasmaField.getTimestamp(), new FullSolarWindData(
                                plasmaField.getDensity(),
                                plasmaField.getSpeed(),
                                magField.getBzGsm(),
                                magField.getBt()));
                }
            }
        }
        return map;
    }

    private PlasmData getPlasmData(JsonElement plasmLine) {
        PlasmData plasmaField;
        try {
            String replacedArrayP2 = plasmLine.toString().replaceAll("[+^\"\\[\\]]","");
            String[] p2temp = replacedArrayP2.split(",");
            Timestamp timeTagPlasm = Timestamp.valueOf(p2temp[0]);
            double density = Double.parseDouble(p2temp[1]);
            double speed = Double.parseDouble(p2temp[2]);
            plasmaField= new PlasmData(timeTagPlasm, density, speed);
        } catch (NumberFormatException e) {
            out.println("Caught parse error " + e);
            return null;
        }
        return plasmaField;
    }

    private MagData getMagData(JsonElement magLine) {
        MagData magField;
        try {
            String replacedArrayP1 = magLine.toString().replaceAll("[+^\"\\[\\]]","");
            String[] p1temp = replacedArrayP1.split(",");
            Timestamp timeTagMag = Timestamp.valueOf(p1temp[0]);
            double bzGsm = Double.parseDouble(p1temp[3]);
            double bt = Double.parseDouble(p1temp[6]);
            magField = new MagData(timeTagMag, bzGsm, bt);
        } catch (NumberFormatException e) {
            out.println("Caught parse error " + e);
            return null;
        }
        return magField;
    }

    private void cleanIndex(JsonArray mag, JsonArray plasm) {
        mag.remove(0);
        plasm.remove(0);
    }
}
