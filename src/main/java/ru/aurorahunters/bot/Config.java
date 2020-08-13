package ru.aurorahunters.bot;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Config {
    //  Database credentials
    private static String DB_URL;
    private static String USER;
    private static String PASS;
    private static Connection connection;
    private static String bot_username;
    private static String bot_token;
    private static String project_site;
    private static boolean isConfigCreated;

    public static void loadConfig() {
        FileInputStream config;
        Properties properties = new Properties();
        try {
            config = new FileInputStream("config.properties");
            properties.load(config);
            DB_URL = properties.getProperty("db.host");
            USER = properties.getProperty("db.login");
            PASS = properties.getProperty("db.password");
            bot_username = properties.getProperty("bot.username");
            bot_token = properties.getProperty("bot.token");
            project_site = properties.getProperty("bot.site");
            isConfigCreated = true;
            setDbConnection();
        } catch (IOException e) {
            System.err.println("Error: config.properties is not exist in program folder.");
            isConfigCreated = false;
        }
    }

    private static void setDbConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Error: seems like database is not available or its credentials is not correct.");
            System.exit(0);
        }
    }

    public static Connection getDbConnection() {
        return connection;
    }

    public static String getBot_username() {
        return bot_username;
    }

    public static String getBot_token() {
        return bot_token;
    }

    public static String getProject_site() {
        return project_site;
    }

    public static boolean isIsConfigCreated() {
        return isConfigCreated;
    }
}
