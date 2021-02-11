package ru.aurorahunters.bot.service.solarwind;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.dao.DataDAO;
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
    private LinkedHashMap<String, ArrayList<String>> mergeJson(JsonArray mag, JsonArray plasm) {
        //TODO refactor this code-smell method
        LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<>();
        for (JsonElement plasmLine : plasm) {
            String replacedArrayP2 = plasmLine.toString().replaceAll("[+^\"\\[\\]]","");
            String[] p2temp;
            p2temp = replacedArrayP2.split(",");
            String timeTagPlasm = p2temp[0];
            String density = p2temp[1];
            String speed = p2temp[2];
            for (JsonElement magLine : mag) {
                String replacedArrayP1 = magLine.toString().replaceAll("[+^\"\\[\\]]","");
                String[] p1temp;
                p1temp = replacedArrayP1.split(",");
                String timeTagMag = p1temp[0];
                String bzGsm = p1temp[3];
                String bt = p1temp[6];
                if (timeTagMag.equals(timeTagPlasm)) {
                    if (density.equals("null") || speed.equals("null")
                            || bzGsm.equals("null") || bt.equals("null")) {
                        continue;
                    }
                    map.put(timeTagPlasm, new ArrayList<>(
                            Arrays.asList(density, speed, bzGsm, bt)));
                }
            }
        }
        map.remove("time_tag"); // Deletes "time_tag" entry from future json as unnecessary
        return map;
    }
}