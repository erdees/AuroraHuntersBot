package ru.aurorahunters.bot.dao;

import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.enums.MagnetEnum;
import ru.aurorahunters.bot.model.chart.MagnetChart;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MagnetometerDAO {

    /**
     * A method which puts magnetometer HashMap to a Database.
     * @param m HashMap with magnetometer data.
     */
    public void insertValues(Map<Timestamp, ArrayList<Double>> m, MagnetEnum type)
            throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        String sql = "INSERT INTO " + type.getDbTableName() +
                " VALUES (?::TIMESTAMP, ?::NUMERIC, ?::NUMERIC, ?::NUMERIC) " +
                "ON CONFLICT (time_tag) DO NOTHING;";
        try (PreparedStatement ps = Config.getDbConnection().prepareStatement(sql)) {
            for (Map.Entry<Timestamp, ArrayList<Double>> entry : m.entrySet()) {
                Timestamp key = entry.getKey();
                ArrayList<Double> value = entry.getValue();
                Object[] arr = value.toArray();
                ps.setTimestamp(1, key);
                ps.setObject(2, arr[0]);
                ps.setObject(3, arr[1]);
                ps.setObject(4, arr[2]);
                ps.executeUpdate();
            }
        }
        Config.getDbConnection().commit();
    }

    /**
     * Method which retrieves a HashMap with daily magnetometers data.
     * @return a Map with daily magnetometers data
     */
    public SortedMap<Date, ArrayList<Double>> getDailyValues(MagnetChart chart)
            throws SQLException, ParseException {
        TreeMap<Date, ArrayList<Double>> out = new TreeMap<>();
        String query =
                "WITH t AS (SELECT time_tag at time zone 'utc/" + chart.getTimezone() +
                "' at time zone 'utc',  mag_x, mag_y, mag_z from " +
                        chart.getMagnetEnum().getDbTableName() +
                " ORDER BY time_tag desc limit 8640) SELECT * FROM t ORDER BY timezone ASC";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(query)) {
            resultSet = preparedStatement.executeQuery();
        }
        while (resultSet.next()) {
            Timestamp timeTag = resultSet.getTimestamp(1);
            double x = resultSet.getDouble(2);
            double y = resultSet.getDouble(3);
            double z = resultSet.getDouble(4);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeTag.toString());
            out.put(date, new ArrayList<>(Arrays.asList(x, y, z)));
        }
        return out;
    }
}
