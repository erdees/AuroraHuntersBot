package ru.aurorahunters.bot.dao;

import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.telegram.MessageHandler;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.*;

public class SessionsDAO {

    /**
     * Method removes user session with given chat id.
     * @param chatId which is Telegram unique chat identifier.
     */
    public void deleteUserById(Long chatId) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        String deletionQuery = "DELETE FROM sessions WHERE chat_id = ?;";
        try (PreparedStatement ps = Config.getDbConnection().prepareStatement(deletionQuery)) {
            try {
                ps.setLong(1, chatId);
                ps.executeUpdate();
                Config.getDbConnection().commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /** Return number of users from database. */
    public int getUserCount() throws SQLException {
        int totalUsersRegistered = 0;
        String returnUserCount = "select COUNT(*) from sessions";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement =
                     Config.getDbConnection().prepareStatement(returnUserCount)) {
            resultSet = preparedStatement.executeQuery();
        }
        while (resultSet.next()) {
            totalUsersRegistered = resultSet.getInt(1);
        }
        return totalUsersRegistered;
    }

    /**
     * Checks if there is a requested chatID in the Database.
     * @param chatID which is Telegram unique chat identifier.
     * @return boolean value according to the check result.
     */
    public boolean isChatIdExist(Long chatID) throws SQLException {
        final String SQL_SELECT = "select chat_id from sessions where chat_id=?;";
        ResultSet resultSet;
        try (PreparedStatement ps = Config.getDbConnection().prepareStatement(SQL_SELECT)) {
            ps.setLong(1, chatID);
            resultSet = ps.executeQuery();
        }
        return resultSet.next();
    }

    /**
     * When new user should be created, method creates a row with default user fields in the
     * Database.
     * @param chatId which is Telegram unique chat identifier.
     */
    public void insertNewUser(Long chatId) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        String sql = "INSERT INTO sessions VALUES " +
                "(?::NUMERIC, ?::BOOLEAN, ?::BOOLEAN, ?::TEXT, ?::BOOLEAN, ?::TEXT, ?::BOOLEAN) " +
                "ON CONFLICT (chat_id) DO NOTHING;";
        try (PreparedStatement ps = Config.getDbConnection().prepareStatement(sql)) {
            try {
                ps.setLong(1, chatId);
                ps.setBoolean(2, true);
                ps.setBoolean(3, false);
                ps.setString(4, "+00:00");
                ps.setBoolean(5, false);
                ps.setString(6, null);
                ps.setBoolean(7, true);
                ps.executeUpdate();
                Config.getDbConnection().commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return bot object instance with a requested fields from the Database.
     * @param chatId which is Telegram unique chat identifier.
     * @return bot object with mandatory fields.
     */
    public MessageHandler getCurrentChat(Long chatId) throws SQLException {
        MessageHandler bot = null;
        final String sql = "SELECT * FROM sessions WHERE chat_id= ?;";
        ResultSet resultSet;
        try (PreparedStatement ps = Config.getDbConnection().prepareStatement(sql)) {
            ps.setLong(1, chatId);
            resultSet = ps.executeQuery();
        }
        while (resultSet.next()) { //if didn't retrieve object,
            Long id = resultSet.getLong(1);
            boolean isStarted = resultSet.getBoolean(2);
            boolean isTimezone = resultSet.getBoolean(3);
            String timezone = resultSet.getString(4);
            boolean isArchive = resultSet.getBoolean(5);
            String archive = resultSet.getString(6);
            bot = new MessageHandler(id,isStarted,isTimezone,timezone,isArchive,archive);
        }
        return bot;
    }

    /** Configure archive date in a Database as a user parameter which can be used later. */
    public void setDbHistoryDate(String date, long chatId) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        final String SQL = "UPDATE sessions SET is_archive=?, archive=? where chat_id=?;";
        try (PreparedStatement ps = Config.getDbConnection().prepareStatement(SQL)) {
            try {
                ps.setBoolean(1, true);
                ps.setString(2, date);
                ps.setLong(3, chatId);
                ps.executeUpdate();
                Config.getDbConnection().commit();
            } catch (Exception e) {
                out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /** Method which adjust a timezone in a Database. */
    public void setDbTimezone(String timezone, long chatId) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        final String SQL = "UPDATE sessions SET is_timezone=?, timezone=? where chat_id=?;";
        try (PreparedStatement ps = Config.getDbConnection().prepareStatement(SQL)) {
            try {
                ps.setBoolean(1, true);
                ps.setString(2, timezone);
                ps.setLong(3, chatId);
                ps.executeUpdate();
                Config.getDbConnection().commit();
            } catch (Exception e) {
                out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Method which turns on/off notifications on high solar wind parameters by UPDATE query in a Database.
     * @param param boolean where true is on and false is off.
     */
    public void setDbNotif(boolean param, long chatId) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        final String SQL = "UPDATE sessions SET is_notif=? where chat_id=?;";
        try (PreparedStatement ps = Config.getDbConnection().prepareStatement(SQL)) {
            try {
                ps.setBoolean(1, param);
                ps.setLong(2, chatId);
                ps.executeUpdate();
                Config.getDbConnection().commit();
            } catch (Exception e) {
                out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Request from a Database chat Ids with users who subscribed to notifications.
     */
    public List<Long> getSubscribersList() throws SQLException {
        List<Long> subscribersList = new ArrayList<>();
        final String sql = "SELECT chat_id FROM sessions WHERE is_notif='true'";
        ResultSet resultSet;
        try (PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(sql)) {
            resultSet = preparedStatement.executeQuery();
        }
        while (resultSet.next()) {
            long chatId  = resultSet.getLong(1);
            subscribersList.add(chatId);
        }
        return subscribersList;
    }
}
