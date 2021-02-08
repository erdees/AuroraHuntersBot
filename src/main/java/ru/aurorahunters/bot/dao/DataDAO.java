package ru.aurorahunters.bot.dao;

import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.model.ActualSunChart;
import ru.aurorahunters.bot.model.HistoricalDate;
import ru.aurorahunters.bot.model.SolarWindData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataDAO {

    /** This method puts merged json map to the Database */
    public void insertResults(Map<String, ArrayList<String>> map) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        String sql =
                "INSERT INTO data VALUES (?::TIMESTAMP, ?::NUMERIC, ?::NUMERIC, ?::NUMERIC) " +
                        "ON CONFLICT (time_tag) DO NOTHING;";
        try (PreparedStatement ps = Config.getDbConnection().prepareStatement(sql)) {
            try {
                for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    ArrayList<String> value = entry.getValue();
                    Object[] arr = value.toArray();
                    ps.setTimestamp(1, Timestamp.valueOf(key));
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

    /**
     * Method which retrieves from a Database values for chart generation in "online" mode e.g.
     * 180 last values.
     * @param chart parameter which determines which for which timezone chart should be generated.
     * as well as GraphTypeEnum inside with required for each graph fields.
     * @return TreeMap with Date and Double values which is datasource for final chart generation.
     */
    public SortedMap<Date, Double> getCurrentChart(ActualSunChart chart)
            throws SQLException, ParseException {
        TreeMap<Date, Double> out = new TreeMap<>();
        String query =
                "WITH t AS (SELECT time_tag at time zone 'utc/" + chart.getTimezone() +
                        "' at time zone 'utc', " + chart.getGraphTypeEnum().getDbKey() +
                        " from data ORDER BY time_tag desc limit 180) " +
                "SELECT * FROM t ORDER BY timezone ASC";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(query)) {
            resultSet = preparedStatement.executeQuery();
        }
        while (resultSet.next()) {
            Timestamp timeTag = resultSet.getTimestamp(1);
            double value = resultSet.getDouble(2);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeTag.toString());
            out.put(date, value);
        }
        return out;
    }

    /** Return number of entries from database for solar wind parameters. */
    public int getEntriesCount() throws SQLException {
        int totalEntries = 0;
        String entriesCount = "select COUNT(*) from data";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement =
                     Config.getDbConnection().prepareStatement(entriesCount)) {
            resultSet = preparedStatement.executeQuery();
        }
        while (resultSet.next()) {
            totalEntries = resultSet.getInt(1);
        }
        return totalEntries;
    }

    /** Retrieves form a Database latest 5 values, which is necessary to check if notifications
     * should be sent. */
    public List<SolarWindData> getNotificationValues() throws SQLException {
        List<SolarWindData> latestWindData = new ArrayList<>();
        String query =
                "WITH t AS (SELECT time_tag at time zone 'utc/+00:00' at time zone 'utc', " +
                        "density, speed, bz_gsm from data ORDER BY time_tag desc limit 5) " +
                        "SELECT * FROM t ORDER BY timezone ASC";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(query)) {
            resultSet = preparedStatement.executeQuery();
        }
        while (resultSet.next()) {
            double density = resultSet.getDouble(2);
            double speed = resultSet.getDouble(3);
            double bzGsm = resultSet.getDouble(4);
            latestWindData.add(new SolarWindData(density, speed, bzGsm));
        }
        return latestWindData;
    }

    /**
     * Retrieves last 10 density, speed and bz values from Database.
     * @param timezone which goes from MessageHandler.
     * @return a Map with prepared for further actions data.
     */
    public SortedMap<Date, SolarWindData> getLatestValues(String timezone) throws SQLException,
            ParseException {
        TreeMap<Date, SolarWindData> latestWindData = new TreeMap<>();
        String query =
                "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone +
                "' at time zone 'utc', density, speed, bz_gsm from data ORDER BY time_tag desc limit 10) SELECT * " +
                "FROM t ORDER BY timezone ASC";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(query)) {
            resultSet = preparedStatement.executeQuery();
        }
        while (resultSet.next()) {
            Timestamp timeTag = resultSet.getTimestamp(1);
            double density = resultSet.getDouble(2);
            double speed = resultSet.getDouble(3);
            double bzGsm = resultSet.getDouble(4);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeTag.toString());
            latestWindData.put(date, new SolarWindData(density, speed, bzGsm));
        }
        return latestWindData;
    }

    /**
     * Retrieves last 24 hours solar wind values, speed and bz values from Database.
     * @param range determinate  range for database request.
     * @return a Map with prepared for further actions data.
     */
    public SortedMap<Date, SolarWindData> getHistoryValues(HistoricalDate range) throws SQLException,
            ParseException {
        TreeMap<Date, SolarWindData> latestWindData = new TreeMap<>();
        String query =
                "SELECT time_tag, density, speed, bz_gsm FROM data WHERE time_tag >= '"
                        + range.getInitialDate() + "' AND time_tag < '" + range.getEndDate() + "'";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(query)) {
            resultSet = preparedStatement.executeQuery();
        }
        while (resultSet.next()) {
            Timestamp timeTag = resultSet.getTimestamp(1);
            double density = resultSet.getDouble(2);
            double speed = resultSet.getDouble(3);
            double bzGsm = resultSet.getDouble(4);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeTag.toString());
            latestWindData.put(date, new SolarWindData(density, speed, bzGsm));
        }
        return latestWindData;
    }

    /** Return number of entries from database for solar wind parameters. */
    public double getLastSpeed() throws SQLException {
        double lastSpeed = 0;
        String entriesCount = "SELECT speed from data ORDER BY time_tag desc limit 1";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement =
                     Config.getDbConnection().prepareStatement(entriesCount)) {
            resultSet = preparedStatement.executeQuery();
        }
        while (resultSet.next()) {
            lastSpeed = resultSet.getDouble(1);
        }
        return lastSpeed;
    }
}
