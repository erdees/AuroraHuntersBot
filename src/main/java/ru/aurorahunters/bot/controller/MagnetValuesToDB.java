package ru.aurorahunters.bot.controller;

import ru.aurorahunters.bot.Config;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class MagnetValuesToDB implements Runnable {

    private MagnetometerTypeEnum type;
    private boolean isDailyData;

    public MagnetValuesToDB(MagnetometerTypeEnum type, boolean isDailyData) {
        this.type = type;
        this.isDailyData = isDailyData;
    }

    /** Requests a magnetometer data according to a condition (1 hour or daily) in a service mode from Scheduler. */
    @Override
    public void run() {
        try {
            if (isDailyData) {
                writeResultsToDb((new GetMagnetData(type).getDailyMapMagnetData()));
            } else  {
                writeResultsToDb(new GetMagnetData(type).getLatestMapMagnetData());
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method which puts magnetometer HashMap to a Database.
     * @param m HashMap with magnetometer data.
     * @throws SQLException
     */
    private void writeResultsToDb(Map<Timestamp, ArrayList<Double>> m) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        String sql = "INSERT INTO " + type.dbTableName +
                " VALUES (?::TIMESTAMP, ?::NUMERIC, ?::NUMERIC, ?::NUMERIC) ON CONFLICT (time_tag) DO NOTHING;";
        PreparedStatement ps = Config.getDbConnection().prepareStatement(sql);
        for(Map.Entry<Timestamp, ArrayList<Double>> entry : m.entrySet()) {
            Timestamp key = entry.getKey();
            ArrayList<Double> value = entry.getValue();
            Object[] arr = value.toArray();
            ps.setTimestamp (1, key);
            ps.setObject(2, arr[0]);
            ps.setObject(3, arr[1]);
            ps.setObject(4, arr[2]);
            ps.executeUpdate();
        }
        Config.getDbConnection().commit();
    }
}


