package ru.aurorahunters.bot;

import ru.aurorahunters.bot.controller.JsonToDB;
import ru.aurorahunters.bot.notification.Notification;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Config {
    private static String DB_URL;
    private static String USER;
    private static String PASS;
    private static Connection CONNECTION;
    private static String BOT_USERNAME;
    private static String BOT_TOKEN;
    private static String WEBSITE;
    private static int NOTIFY_INTERVAL;
    private static int JSON_TO_DB_SYNC_ID;
    private static String MAG_5MIN;
    private static String PLASM_5MIN;
    private static String MAG_2H;
    private static String PLASM_2H;
    private static String MAG_24H;
    private static String PLASM_24H;
    private static String MAG_7DAY;
    private static String PLASM_7DAY;
    private static String GRAPH_COLOR_LOW;
    private static String GRAPH_COLOR_NORMAL;
    private static String GRAPH_COLOR_MODERATE;
    private static String GRAPH_COLOR_HIGH;
    private static String GRAPH_COLOR_VERYHIGH;

    /** Try to load and parse config.properties */
    public static void loadConfig() {
        FileInputStream config;
        Properties properties = new Properties();
        try {
            config = new FileInputStream("config.properties");
            properties.load(config);
        } catch (IOException e) {
            System.err.println("Error: config.properties is not exist in program folder.");
            System.exit(0);
        }
        try {
            DB_URL = properties.getProperty("db.host");
            USER = properties.getProperty("db.login");
            PASS = properties.getProperty("db.password");
            BOT_USERNAME = properties.getProperty("bot.username");
            BOT_TOKEN = properties.getProperty("bot.token");
            WEBSITE = properties.getProperty("bot.site");
            NOTIFY_INTERVAL = Integer.parseInt(properties.getProperty("bot.interval"));
            JSON_TO_DB_SYNC_ID = Integer.parseInt(properties.getProperty("bot.recovery"));
            MAG_5MIN = properties.getProperty("json.mag.5min");
            PLASM_5MIN = properties.getProperty("json.plasma.5min");
            MAG_2H = properties.getProperty("json.mag.2h");
            PLASM_2H = properties.getProperty("json.plasma.2h");
            MAG_24H = properties.getProperty("json.mag.24h");
            PLASM_24H = properties.getProperty("json.plasma.24h");
            MAG_7DAY = properties.getProperty("json.mag.7day");
            PLASM_7DAY = properties.getProperty("json.plasma.7day");

            GRAPH_COLOR_LOW = properties.getProperty("graph.color.low");
            GRAPH_COLOR_NORMAL = properties.getProperty("graph.color.normal");
            GRAPH_COLOR_MODERATE = properties.getProperty("graph.color.moderate");
            GRAPH_COLOR_HIGH = properties.getProperty("graph.color.high");
            GRAPH_COLOR_VERYHIGH = properties.getProperty("graph.color.veryhigh");

            setDbConnection();
        } catch (Exception e) {
            System.err.println("Error: seems like config.properties has wrong parameters. Please check " +
                    "config.properties file syntax and try again.");
            System.exit(0);
        }
    }

    /** Try to establish a connection with the Database */
    private static void setDbConnection() {
        try {
            CONNECTION = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Error: seems like PostgreSQL database is not available or its credentials is not correct.");
            System.exit(0);
        }
    }

    /** Scheduler settings */
    public static void initializeScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService notifScheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(new JsonToDB(Config.getJsonToDbSyncId()), 0, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new JsonToDB(1), 0, 40, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new JsonToDB(2), 59, 60, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(new JsonToDB(4), 48, 48, TimeUnit.HOURS);
        notifScheduler.scheduleAtFixedRate(new Notification(), 1, Config.getNotifyInterval(), TimeUnit.MINUTES);
    }

    public static Connection getDbConnection() {
        return CONNECTION;
    }

    public static String getBotUsername() {
        return BOT_USERNAME;
    }

    public static String getBotToken() {
        return BOT_TOKEN;
    }

    public static String getWEBSITE() {
        return WEBSITE;
    }

    public static int getNotifyInterval() {
        return NOTIFY_INTERVAL;
    }

    public static int getJsonToDbSyncId() {
        return JSON_TO_DB_SYNC_ID;
    }

    public static String getMag5min() {
        return MAG_5MIN;
    }

    public static String getPlasm5min() {
        return PLASM_5MIN;
    }

    public static String getMag2h() {
        return MAG_2H;
    }

    public static String getPlasm2h() {
        return PLASM_2H;
    }

    public static String getMag24h() {
        return MAG_24H;
    }

    public static String getPlasm24h() {
        return PLASM_24H;
    }

    public static String getMag7day() {
        return MAG_7DAY;
    }

    public static String getPlasm7day() {
        return PLASM_7DAY;
    }

    public static String getGraphColorLow() {
        return GRAPH_COLOR_LOW;
    }

    public static String getGraphColorNormal() {
        return GRAPH_COLOR_NORMAL;
    }

    public static String getGraphColorModerate() {
        return GRAPH_COLOR_MODERATE;
    }

    public static String getGraphColorHigh() {
        return GRAPH_COLOR_HIGH;
    }

    public static String getGraphColorVeryhigh() {
        return GRAPH_COLOR_VERYHIGH;
    }
}
