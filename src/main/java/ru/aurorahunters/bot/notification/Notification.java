package ru.aurorahunters.bot.notification;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.telegram.AuroraBot;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Notification implements Runnable  {
    static ArrayList<Double> densityValues = new ArrayList<>();
    static ArrayList<Double> speedValues = new ArrayList<>();
    static ArrayList<Double> bz_gsmValues = new ArrayList<>();
    private static final double topDensity = Config.getTopDensity();
    private static final double topSpeed = Config.getTopSpeed();
    private static final double topBz = Config.getTopBz();
    private static int minuteCounter;

    /**
     * Run method which is necessary to run it as a daemon using ScheduledExecutorService.
     */
    @Override
    public void run() {
        try {
            try {
                if (checkNotification()) {
                    if (minuteCounter()) {
                        sendNotif(getAlarmString());
                        minuteCounter = 0;
                    }
                    minuteCounter++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Caught exception in ScheduledExecutorService. StackTrace:\n" + e);
        }
    }

    /**
     * Send a notification message to all bot users who subscribed to notifications.
     * @param message message which should be sent.
     */
    public void sendNotif(String message) throws SQLException, InterruptedException {
        long chatId;
        final String sql = "SELECT chat_id FROM sessions WHERE is_notif='true'";
        PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            chatId  = resultSet.getLong(1);
            AuroraBot notificationEvent = new AuroraBot();
            SendMessage notificationMessage = new SendMessage(chatId,message).setParseMode(ParseMode.HTML);
            try {
                notificationEvent.execute(notificationMessage);
            } catch (TelegramApiException e) {
                System.out.println("Caught exception while send broadcast messages. StackTrace:\n" + e);
            }
            Thread.sleep(35);
        }
    }

    /**
     * Generates a String message in Telegram format with notification.
     * @return a String with result.
     */
    public String getAlarmString() {
        DecimalFormat chatOutput = new DecimalFormat("###.#");
        String firstLine = String.format("%s%s%n", "<pre>","Notification: high solar wind parameters:\n");
        String secondLine = String.format("%4s\t%s\t%3s\t%s\t%4s%n", "BZ ", "|", "S ", "|", "PD");
        String lastLine = "</pre>\nclick to see latest data: /last\n" +
                "click /notif_off to disable notifications";
        StringBuilder sb = new StringBuilder();
        sb.append(firstLine);
        sb.append(secondLine);
        Double tempDensity = densityValues.get(densityValues.size()-1);
        Double tempSpeed = speedValues.get(speedValues.size()-1);
        Double tempBz = bz_gsmValues.get(bz_gsmValues.size()-1);
        sb.append(String.format("%4s\t%s\t%3s\t%s\t%4s%n", chatOutput.format(tempBz), "|",
                Math.round(tempSpeed), "|", chatOutput.format(tempDensity)));
        sb.append(lastLine);
        return sb.toString();
    }

    /** Check if one, two, three or none of parameters is true. */
    public boolean checkNotification() throws SQLException {
        getDbValues();
        return isDensityHigh() || isSpeedHigh() || isBzHigh();
    }

    /**
     * Method which compares values from ArrayList, and if 5 of them is more or equals topDensity, true will returned.
     * @return boolean with a result of check.
     */
    private boolean isDensityHigh() {
        int count = 0;
        for (Double d : densityValues) {
            if (d > topDensity) {
                count++;
            }
        }
        return count >= 5;
    }

    /**
     * Method which compares values from ArrayList, and if 5 of them is more or equals topSpeed, true will returned.
     * @return boolean with a result of check.
     */
    private boolean isSpeedHigh() {
        int count = 0;
        for (Double d : speedValues) {
            if (d > topSpeed) {
                count++;
            }
        }
        return count >= 5;
    }

    /**
     * Method which compares values from ArrayList, and if 5 of them is more or equals topBz, true will returned.
     * @return boolean with a result of check.
     */
    private boolean isBzHigh() {
        int count = 0;
        for (Double d : bz_gsmValues) {
            if (d <= topBz) {
                count++;
            }
        }
        return count >= 5;
    }

    /** Retrieves form a Database values which is necessary to check if notifications should be sent. */
    private void getDbValues() throws SQLException {
        resetCollection();
        final String SQL_SELECT = "WITH t AS (SELECT time_tag at time zone 'utc/+00:00' " +
                "at time zone 'utc', density, speed, bz_gsm from data ORDER BY time_tag desc limit 5) SELECT * " +
                "FROM t ORDER BY timezone ASC";
        PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(SQL_SELECT);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            double density = resultSet.getDouble(2);
            double speed = resultSet.getDouble(3);
            double bz_gsm = resultSet.getDouble(4);
            densityValues.add(density);
            speedValues.add(speed);
            bz_gsmValues.add(bz_gsm);
        }
    }

    /** Deletes a temporary ArrayList content for a next iteration. */
    private void resetCollection() {
        densityValues.clear();
        speedValues.clear();
        bz_gsmValues.clear();
    }

    private boolean minuteCounter() {
        return minuteCounter == 0 || minuteCounter == Config.getNotifyInterval();
    }
}