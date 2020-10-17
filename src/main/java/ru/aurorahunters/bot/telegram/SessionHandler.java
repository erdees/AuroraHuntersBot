package ru.aurorahunters.bot.telegram;

import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.utils.TimeClass;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class SessionHandler {

    /**
     * Method which create, delete and hanlde user sessions.
     * @param input incoming to the Bot text message which should be processed.
     * @param chatId which is Telegram unique chat identifier.
     * @param gps Location object (optional) to configure a target chat timezone according to a given Location.
     * @return String with a message which will be sent to a target chat.
     */
    public static String sessionHandler(String input, Long chatId, Location gps)
            throws ParseException, SQLException, IOException,
            TelegramApiException {
        if (!isChatIdExist(chatId)) {
            if (input.equals("/start") || input.equals("/start" + Config.getBotUsername())) {
                setDefaults(chatId);
                return getCurrentChat(chatId).setBotStarted();
            } else return new MessageHandler(chatId).respondMessage(input);
        } else {
            if (input.equals("/stop") || input.equals("/stop" + Config.getBotUsername())) {
                removeId(chatId);
                return "Bot stopped.";
            } else if (gps != null) {
                return getCurrentChat(chatId).setGpsTimezone(TimeClass.getGpsTimezone(gps));
            }
        } return getCurrentChat(chatId).respondMessage(input);
    }

    /**
     * Checks if there is a requested chatID in the Database.
     * @param chatID which is Telegram unique chat identifier.
     * @return boolean value according to the check result.
     */
    public static boolean isChatIdExist(Long chatID) throws SQLException {
        final String SQL_SELECT = "select chat_id from sessions where chat_id=?;";
        PreparedStatement ps = Config.getDbConnection().prepareStatement(SQL_SELECT);
        ps.setLong(1, chatID);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.next();
    }

    /**
     * When new user should be created,  methid creates a row with default user fields in the Database.
     * @param chatId which is Telegram unique chat identifier.
     */
    public static void setDefaults(Long chatId) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        final String sql = "INSERT INTO sessions VALUES (?::NUMERIC, ?::BOOLEAN, ?::BOOLEAN, ?::TEXT, ?::BOOLEAN, " +
                "?::TEXT, ?::BOOLEAN) ON CONFLICT (chat_id) DO NOTHING;";
        PreparedStatement ps = Config.getDbConnection().prepareStatement(sql);
        try {
            ps.setLong(1, chatId);
            ps.setBoolean(2, true);
            ps.setBoolean(3, false);
            ps.setString(4,"+00:00");
            ps.setBoolean(5, false);
            ps.setString(6, null);
            ps.setBoolean(7, true);
            ps.executeUpdate();
            Config.getDbConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method removes user session with given chat id.
     * @param chatId which is Telegram unique chat identifier.
     */
    public static void removeId(Long chatId) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        final String sql = "DELETE FROM sessions WHERE chat_id = ?;";
        PreparedStatement ps = Config.getDbConnection().prepareStatement(sql);
        try {
            ps.setLong(1, chatId);
            ps.executeUpdate();
            Config.getDbConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return bot object instance with a requested fields from the Database.
     * @param chatId which is Telegram unique chat identifier.
     * @return bot object with mandatory fields.
     */
    public static MessageHandler getCurrentChat(Long chatId) throws SQLException {
        MessageHandler bot = null;
        final String sql = "SELECT * FROM sessions WHERE chat_id= ?;";
        PreparedStatement ps = Config.getDbConnection().prepareStatement(sql);
        ps.setLong(1, chatId);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) { //if didn't retreive object,
            Long chat_id = resultSet.getLong(1);
            boolean is_started = resultSet.getBoolean(2);
            boolean is_timezone = resultSet.getBoolean(3);
            String timezone = resultSet.getString(4);
            boolean is_archive = resultSet.getBoolean(5);
            String archive = resultSet.getString(6);
            bot = new MessageHandler(chat_id,is_started,is_timezone,timezone,is_archive,archive);
        }
        return bot;
    }
}