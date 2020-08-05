package ru.aurorahunters.bot;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Notification implements Runnable  {
    static ArrayList<Double> densityValues = new ArrayList<Double>();
    static ArrayList<Double> speedValues = new ArrayList<Double>();
    static ArrayList<Double> bz_gsmValues = new ArrayList<Double>();
    private static final double topDensity = 15.1;
    private static final double topSpeed = 501.0;
    private static final double topBz = -5.1;

    @Override
    public void run() {
        try {
            try {
                if (checkNotification()) {
                    sendNotif(getAlarmString());
                    Thread.sleep(TimeUnit.SECONDS.toMillis(300));
                }
            } catch (SQLException | ParseException | TelegramApiException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Caught exception in ScheduledExecutorService. StackTrace:\n" + e);
        }
    }

    public void sendNotif(String message) throws TelegramApiException, SQLException {
        Long chatId;
        final String sql = "SELECT chat_id FROM sessions WHERE is_notif='true';";
        PreparedStatement preparedStatement = DBconnection.getConnection().prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            chatId  = resultSet.getLong(1);
            AuroraBot notificationEvent = new AuroraBot();
            SendMessage notificationMessage = new SendMessage(chatId,message).setParseMode(ParseMode.HTML);
            notificationEvent.execute(notificationMessage);
        }
    }

    public String getAlarmString() {
        DecimalFormat chatOutput = new DecimalFormat("###.#");
        String formattedString = "";
        String firstLine = String.format("%s%s%n", "<pre>","Notification: high solar wind parameters:\n");
        String secondLine = String.format("%4s\t%s\t%3s\t%s\t%4s%n", "BZ ", "|", "S ", "|", "PD");
        String lastLine = "</pre>\nclick to see latest data: /last\n" +
                "to disable notifications, click /notif_off";
        StringBuilder sb = new StringBuilder();
        sb.append(firstLine);
        sb.append(secondLine);
        Double tempDensity = densityValues.get(densityValues.size()-1);
        Double tempSpeed = speedValues.get(speedValues.size()-1);
        Double tempBz = bz_gsmValues.get(bz_gsmValues.size()-1);
        sb.append(String.format("%4s\t%s\t%3s\t%s\t%4s%n", chatOutput.format(tempBz), "|",
                Math.round(tempSpeed), "|", chatOutput.format(tempDensity)));
        sb.append(lastLine);
        formattedString = sb.toString();
        return formattedString;
    }

    public boolean checkNotification() throws SQLException, ParseException {
        getDbValues();
        return isDensityHigh() || isSpeedHigh() || isBzHigh();
    }

    private boolean isDensityHigh() {
        int count = 0;
        for (Double d : densityValues) {
            if (d > topDensity) {
                count++;
            }
        }
        return count >= 5;
    }

    private boolean isSpeedHigh() {
        int count = 0;
        for (Double d : speedValues) {
            if (d > topSpeed) {
                count++;
            }
        }
        return count >= 5;
    }

    private boolean isBzHigh() {
        int count = 0;
        for (Double d : bz_gsmValues) {
            if (d <= topBz) {
                count++;
            }
        }
        return count >= 5;
    }

    private void getDbValues() throws SQLException, ParseException {
        resetCollection();
        final String SQL_SELECT = "WITH t AS (SELECT time_tag at time zone 'utc/+00:00' " +
                "at time zone 'utc', density, speed, bz_gsm from data ORDER BY time_tag desc limit 5) SELECT * " +
                "FROM t ORDER BY timezone ASC;\n";
        PreparedStatement preparedStatement = DBconnection.getConnection().prepareStatement(SQL_SELECT);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double density = resultSet.getDouble(2);
            double speed = resultSet.getDouble(3);
            double bz_gsm = resultSet.getDouble(4);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            String formattedDate = new SimpleDateFormat("HH:mm").format(date); // 9:00
            densityValues.add(density);
            speedValues.add(speed);
            bz_gsmValues.add(bz_gsm);
        }
    }

    private void resetCollection() {
        densityValues.clear();
        speedValues.clear();
        bz_gsmValues.clear();
    }
}
