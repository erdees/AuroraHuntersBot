package ru.aurorahunters.bot;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class GetDataFromDB {
    public static String getLastValues(String timezone) throws SQLException, ParseException {
        final String SQL_SELECT = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone +"' at time zone 'utc', density, speed, bz_gsm from data ORDER BY time_tag desc limit 10) SELECT * FROM t ORDER BY timezone ASC;\n";
        PreparedStatement preparedStatement = DBconnection.getConnection().prepareStatement(SQL_SELECT);
        ResultSet resultSet = preparedStatement.executeQuery();
        String shortTimeZone[] = timezone.split(":");
        String timeZoneToMessage = shortTimeZone[0];
        String temp = "";
        String firstLine = String.format("%s%s%n", "<pre>","Waiting time: " + getWaitingTime() + "\n");
        String secondLine = String.format("%4s\t%s\t%3s\t%s\t%4s\t%s\t%3s%n", "BZ ", "|", "S ", "|", "PD", "|", "UTC" +timeZoneToMessage);
        String lastLine = "</pre>\nlatest density graph /graph_bz" +
                "\nlatest speed graph /graph_speed" +
                "\nlatest bz graph /graph_density" +
                "\nall latest graphs /graph_all";
        StringBuilder sb = new StringBuilder();
        sb.append(firstLine);
        sb.append(secondLine);
        DecimalFormat chatOutput = new DecimalFormat("###.#");
        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double density = resultSet.getDouble(2);
            double speed = resultSet.getDouble(3);
            double bz_gsm = resultSet.getDouble(4);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            String newString = new SimpleDateFormat("HH:mm").format(date); // 9:00

            sb.append(String.format("%4s\t%s\t%3s\t%s\t%4s\t%s\t%3s%n", chatOutput.format(bz_gsm), "|",
                    Math.round(speed), "|", chatOutput.format(density), "|", newString));
            if (resultSet.isLast()) {
                sb.append(lastLine);
                temp = sb.toString();
            }
        }
        return temp;
    }

    public static String getWaitingTime() throws SQLException {
        double speed = 0;
        final String SQL_SPEED_LAST = "SELECT speed from data ORDER BY time_tag desc limit 1;";
        PreparedStatement preparedStatement = DBconnection.getConnection().prepareStatement(SQL_SPEED_LAST);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            speed = resultSet.getDouble(1);
        }
        double calc = 1500000.0 / 60.0 / 60.0 / speed;
        int result = (int) (calc = Math.floor(calc * 10000) / 100);
        int hours = result / 60; //since both are ints, you get an int
        int minutes = result % 60;
        return hours + "h " + minutes + "m";
    }

    public static String getHistoryValues(String date) throws SQLException, ParseException {
        LinkedHashMap<Integer, ArrayList<Double>> map = new LinkedHashMap<Integer, ArrayList<Double>>();
        ArrayList<Double> densitylist = new ArrayList<>();
        ArrayList<Double> speedList = new ArrayList<>();
        ArrayList<Double> bzList = new ArrayList<>();
        String temp = "";
        DecimalFormat chatOutput = new DecimalFormat("###.#");
        if (date.matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")) {
            Date day = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Date nextDay;
            Calendar c = Calendar.getInstance();
            c.setTime(day);
            c.add(Calendar.DATE, 1);
            nextDay = c.getTime();
            String initialDay = new SimpleDateFormat("yyyy-MM-dd").format(day);
            String finalDay = new SimpleDateFormat("yyyy-MM-dd").format(nextDay);
            final String SQL_TEST = "SELECT time_tag, density, speed, bz_gsm FROM data WHERE time_tag >= \'" + initialDay + "\' AND time_tag < \'" + finalDay + "\';";
            PreparedStatement preparedStatement = DBconnection.getConnection().prepareStatement(SQL_TEST);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Timestamp time_tag = resultSet.getTimestamp(1);
                double density = resultSet.getDouble(2);
                double speed = resultSet.getDouble(3);
                double bz_gsm = resultSet.getDouble(4);
                Date sqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
                String hourVar = new SimpleDateFormat("HH").format(sqlDate);
                String minVar = new SimpleDateFormat("mm").format(sqlDate);
                densitylist.add(density);
                bzList.add(bz_gsm);
                speedList.add(speed);
                if (Integer.parseInt(minVar) == 59) {
                    Double maxDensity = Collections.max(densitylist);
                    Double maxSpeed = Collections.max(speedList);
                    Double maxBz = Collections.min(bzList);
                    map.put(Integer.parseInt(hourVar), new ArrayList<Double>(Arrays.asList(maxDensity, maxSpeed, maxBz)));
                    densitylist.removeAll(densitylist);
                    speedList.removeAll(speedList);
                    bzList.removeAll(bzList);
                }
            }
            if (map.isEmpty()) {
                return "Please choose another date. There is no data for " + date;
            } else {
                StringBuilder sb = new StringBuilder();
                String firstLine = String.format("%s%s%n", "<pre>","History values for " + date + "\n");
                String secondLine = String.format("%4s\t%s\t%3s\t%s\t%4s\t%s\t%3s%n", "BZ ", "|", "S", "|", "PT", "|", "Time" );
                sb.append(firstLine);
                sb.append(secondLine);
                for(Map.Entry<Integer, ArrayList<Double>> entry : map.entrySet()) {
                    Integer key = entry.getKey();
                    ArrayList<Double> value = entry.getValue();
                    sb.append(String.format("%4s\t%s\t%3s\t%s\t%4s\t%s\t%3s%n", chatOutput.format(value.get(2)) , "|", Math.round(value.get(1)), "|", chatOutput.format(value.get(0)), "|", key));
                }
                String lastLine = "</pre>";
                sb.append(lastLine);
                temp = sb.toString();
                return temp;
            }
        } else {
            return "Wrong format. Please type correct command.";
        }
    }
}
