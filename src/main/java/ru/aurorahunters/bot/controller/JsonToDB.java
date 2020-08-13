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

public class JsonToDB implements Runnable {
    private String time_tagP1;
    private String time_tagP2;
    private String density;
    private String speed;
    private String bz_gsm;
    private String p1;
    private String p2;
    private LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();
    private static final String mag5minURL = "https://services.swpc.noaa.gov/products/solar-wind/mag-5-minute.json";
    private static final String plasma5minURL = "https://services.swpc.noaa.gov/products/solar-wind/plasma-5-minute.json";
    private static final String mag2hourURL = "https://services.swpc.noaa.gov/products/solar-wind/mag-2-hour.json";
    private static final String plasma2hourURL = "https://services.swpc.noaa.gov/products/solar-wind/plasma-2-hour.json";
    private static final String mag1dayURL = "https://services.swpc.noaa.gov/products/solar-wind/mag-1-day.json";
    private static final String plasma1dayURL = "https://services.swpc.noaa.gov/products/solar-wind/plasma-1-day.json";
    private static final String mag7dayURL = "https://services.swpc.noaa.gov/products/solar-wind/mag-7-day.json";
    private static final String plasma7dayURL = "https://services.swpc.noaa.gov/products/solar-wind/plasma-7-day.json";
    private JsonArray jsonArrayP1;
    private JsonArray jsonArrayP2;
    private int id;

    public JsonToDB(int id) {
        this.id = id;
    }

    public void run() {
        try {
            try {
                fetchJson(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonArray jsonArrayP1 = new Gson().fromJson(p1, JsonArray.class);
            JsonArray jsonArrayP2 = new Gson().fromJson(p2, JsonArray.class);
            try {
                mergeMap(jsonArrayP1, jsonArrayP2);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                dbConnect(); //put merge result to DB
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Caught exception in ScheduledExecutorService. StackTrace:\n" + e);
        }
    }

    private void fetchJson(int id) throws IOException {
        String tempMag = "";
        String tempPlasma = "";
        if (id == 1) {
            tempMag = mag5minURL;
            tempPlasma = plasma5minURL;
        }
        else if (id == 2) {
            tempMag = mag2hourURL;
            tempPlasma = plasma2hourURL;
        }
        else if (id == 3) {
            tempMag = mag1dayURL;
            tempPlasma = plasma1dayURL;
        }
        else if (id == 4) {
            tempMag = mag7dayURL;
            tempPlasma = plasma7dayURL;
        }
        URL magUrl = new URL(tempMag);
        BufferedReader MagIn = new BufferedReader(new InputStreamReader(magUrl.openStream()));
        p1 = MagIn.readLine();
        MagIn.close();
        URL plasmaUrl = new URL(tempPlasma);
        BufferedReader plasmaIn = new BufferedReader(new InputStreamReader(plasmaUrl.openStream()));
        p2 = plasmaIn.readLine();
        plasmaIn.close();
    }

    private void mergeMap(JsonArray p1, JsonArray p2) throws SQLException {
        for (JsonElement e2 : p2) {
            String replacedArrayP2 = e2.toString().replaceAll("[+^\"\\[\\]]","");
            String[] p2temp;
            p2temp = replacedArrayP2.split(",");
            time_tagP2 = p2temp[0];
            density = p2temp[1];
            speed = p2temp[2];
                for (JsonElement e1 : p1) {
                    String replacedArrayP1 = e1.toString().replaceAll("[+^\"\\[\\]]","");
                    String[] p1temp;
                    p1temp = replacedArrayP1.split(",");
                    time_tagP1 = p1temp[0];
                    bz_gsm = p1temp[3];
                    if (time_tagP1.equals(time_tagP2)) {
                        if (!density.equals("null") && !speed.equals("null") && !bz_gsm.equals("null")) {
                            map.put(time_tagP2, new ArrayList<String>(Arrays.asList(density,speed,bz_gsm)));
                        }
                    }
                }
        }
        map.remove("time_tag");
        for(Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
        }
    }

    private void dbConnect() throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        String sql = "INSERT INTO data VALUES (?::TIMESTAMP, ?::NUMERIC, ?::NUMERIC, ?::NUMERIC) ON CONFLICT (time_tag) DO NOTHING;";
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
