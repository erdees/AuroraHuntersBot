package ru.aurorahunters.bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class SessionHandler {

    public static String sessionHandler(String input, Long chatId) throws ParseException, SQLException, IOException,
            TelegramApiException {
        if (!checkId(chatId)) {
            if (input.contains("/start") || input.equals("/start@aurorahunters_bot")) {
                setDefaults(chatId);
                return getCurrentChat(chatId).setBotStarted();
            }
            else return new MessageHandler(chatId).respondMessage(input);
        }
        else {
            if (input.contains("/stop") || input.equals("/stop@aurorahunters_bot")) {
                removeId(chatId);
                return "Bot stopped.";
            }
        }
        return getCurrentChat(chatId).respondMessage(input);
    }
    // REWORK!!!!!
    public static boolean checkId(Long chatID) throws IOException, SQLException, ParseException{
        final String SQL_SELECT = "select chat_id from sessions where chat_id=?;";
        PreparedStatement ps = DBconnection.getConnection().prepareStatement(SQL_SELECT);
        ps.setLong(1, chatID);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.next();
    }

    public static void setDefaults(Long chatId) throws SQLException {
        DBconnection.getConnection().setAutoCommit(false);
        final String sql = "INSERT INTO sessions VALUES (?::NUMERIC, ?::BOOLEAN, ?::BOOLEAN, ?::TEXT, ?::BOOLEAN, ?::TEXT, ?::BOOLEAN) ON CONFLICT (chat_id) DO NOTHING;";
        PreparedStatement ps = DBconnection.getConnection().prepareStatement(sql);
        try {
            ps.setLong(1, chatId);
            ps.setBoolean(2, true);
            ps.setBoolean(3, false);
            ps.setString(4,"+00:00");
            ps.setBoolean(5, false);
            ps.setString(6, null);
            ps.setBoolean(7, true);
            ps.executeUpdate();
            DBconnection.getConnection().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void removeId(Long chatId) throws SQLException {
        DBconnection.getConnection().setAutoCommit(false);
        final String sql = "DELETE FROM sessions WHERE chat_id = ?;";
        PreparedStatement ps = DBconnection.getConnection().prepareStatement(sql);
        try {
            ps.setLong(1, chatId);
            ps.executeUpdate();
            DBconnection.getConnection().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static MessageHandler getCurrentChat(Long chatId) throws SQLException {
        MessageHandler bot = null;
        final String sql = "SELECT * FROM sessions WHERE chat_id= ?;";
        PreparedStatement ps = DBconnection.getConnection().prepareStatement(sql);
        ps.setLong(1, chatId);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            Long chat_id = resultSet.getLong(1);
            boolean is_started = resultSet.getBoolean(2);
            boolean is_timezone = resultSet.getBoolean(3);
            String timezone = resultSet.getString(4);
            boolean is_archive = resultSet.getBoolean(5);
            String archive = resultSet.getString(6);
            boolean is_notif = resultSet.getBoolean(7);
            bot = new MessageHandler(chat_id,is_started,is_timezone,timezone,is_archive,archive,is_notif);
        }
        return bot;
    }
}