package ru.aurorahunters.bot.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ru.aurorahunters.bot.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class SunWindDataToDB implements Runnable {
    private String p1;
    private String p2;
    private LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<>();
    private int id;

    public SunWindDataToDB(int id) {
        this.id = id;
    }

    /**
     * Method which requests json files, merge and put them to the Database according to ScheduledExecutorService
     * settings. To have a relevant values, it is recommended to do it at least every 1 minute.
     */
    public void run() {
        try {
            try {
                getJsonId(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonArray jsonArrayP1 = new Gson().fromJson(p1, JsonArray.class);
            JsonArray jsonArrayP2 = new Gson().fromJson(p2, JsonArray.class);
            mergeMap(jsonArrayP1, jsonArrayP2);
            try {
                writeResultsToDb(); //put merge result to DB
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Caught exception in ScheduledExecutorService. StackTrace:\n" + e);
        }
    }

    /**
     * Choose required json pair according to its id.
     * @param id which is the id of json file pair.
     */
    private void getJsonId(int id) throws IOException {
        String tempMag = "";
        String tempPlasma = "";
        if (id == 1) {
            tempMag = Config.getMag5min();
            tempPlasma = Config.getPlasm5min();
        } else if (id == 2) {
            tempMag = Config.getMag2h();
            tempPlasma = Config.getPlasm2h();
        } else if (id == 3) {
            tempMag = Config.getMag24h();
            tempPlasma = Config.getPlasm24h();
        } else if (id == 4) {
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
        BufferedReader MagIn = new BufferedReader(new InputStreamReader(magUrl.openStream()));
        p1 = MagIn.readLine();
        MagIn.close();
        URL plasmaUrl = new URL(plasma);
        BufferedReader plasmaIn = new BufferedReader(new InputStreamReader(plasmaUrl.openStream()));
        p2 = plasmaIn.readLine();
        plasmaIn.close();
    }

    /**
     * This method merges two different json arrays to a one single map where the map key is time_tag. Two different
     * fetched json files has time_tag relation. For example, when 'mag' and 'plasma' json has the same time_tag, their
     * parameters (bz_gsm, density, speed) should be placed to a map as a ArrayList with the time_tag map key.
     * @param p1 JsonArray object of 'mag' json
     * @param p2 JsonArray object of 'plasma' json
     */
    private void mergeMap(JsonArray p1, JsonArray p2) {
        for (JsonElement e2 : p2) {
            String replacedArrayP2 = e2.toString().replaceAll("[+^\"\\[\\]]","");
            String[] p2temp;
            p2temp = replacedArrayP2.split(",");
            String time_tagP2 = p2temp[0];
            String density = p2temp[1];
            String speed = p2temp[2];
                for (JsonElement e1 : p1) {
                    String replacedArrayP1 = e1.toString().replaceAll("[+^\"\\[\\]]","");
                    String[] p1temp;
                    p1temp = replacedArrayP1.split(",");
                    String time_tagP1 = p1temp[0];
                    String bz_gsm = p1temp[3];
                    if (time_tagP1.equals(time_tagP2)) {
                        if (!density.equals("null") && !speed.equals("null") && !bz_gsm.equals("null")) {
                            map.put(time_tagP2, new ArrayList<>(Arrays.asList(density, speed, bz_gsm)));
                        }
                    }
                }
        }
        map.remove("time_tag"); // Deletes "time_tag" entry from future json as unnecessary
    }

    /** This method puts merged json map to the Database */
    private void writeResultsToDb() throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        String sql = "INSERT INTO data VALUES (?::TIMESTAMP, ?::NUMERIC, ?::NUMERIC, ?::NUMERIC) ON CONFLICT " +
                "(time_tag) DO NOTHING;";
        PreparedStatement ps = Config.getDbConnection().prepareStatement(sql);
        try {
            for(Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> value = entry.getValue();
                Object[] arr = value.toArray();
                ps.setTimestamp (1, Timestamp.valueOf(key));
                ps.setObject(2, arr[0]);
                ps.setObject(3, arr[1]);
                ps.setObject(4, arr[2]);
                ps.executeUpdate();
            }
            Config.getDbConnection().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
