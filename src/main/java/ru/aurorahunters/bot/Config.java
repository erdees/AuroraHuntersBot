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
    private static String GRAPH_COLOR_QUIET;
    private static String GRAPH_COLOR_MODERATE;
    private static String GRAPH_COLOR_INCREASED;
    private static String GRAPH_COLOR_HIGH;
    private static String GRAPH_COLOR_EXTREME;
    private static double TOP_DENSITY;
    private static double TOP_SPEED;
    private static double TOP_BZ;
    private static int GRAPH_RANGE_DENS_QUIET_START;
    private static int GRAPH_RANGE_DENS_QUIET_END;
    private static int GRAPH_RANGE_DENS_MODERATE_START;
    private static int GRAPH_RANGE_DENS_MODERATE_END;
    private static int GRAPH_RANGE_DENS_INCREASED_START;
    private static int GRAPH_RANGE_DENS_INCREASED_END;
    private static int GRAPH_RANGE_DENS_HIGH_START;
    private static int GRAPH_RANGE_DENS_HIGH_END;
    private static int GRAPH_RANGE_DENS_EXTREME_START;
    private static int GRAPH_RANGE_DENS_EXTREME_END;
    private static int GRAPH_RANGE_SPEED_QUIET_START;
    private static int GRAPH_RANGE_SPEED_QUIET_END;
    private static int GRAPH_RANGE_SPEED_MODERATE_START;
    private static int GRAPH_RANGE_SPEED_MODERATE_END;
    private static int GRAPH_RANGE_SPEED_INCREASED_START;
    private static int GRAPH_RANGE_SPEED_INCREASED_END;
    private static int GRAPH_RANGE_SPEED_HIGH_START;
    private static int GRAPH_RANGE_SPEED_HIGH_END;
    private static int GRAPH_RANGE_SPEED_EXTREME_START;
    private static int GRAPH_RANGE_SPEED_EXTREME_END;
    private static double GRAPH_RANGE_BZ_QUIET_START;
    private static double GRAPH_RANGE_BZ_QUIET_END;
    private static double GRAPH_RANGE_BZ_MODERATE_START;
    private static double GRAPH_RANGE_BZ_MODERATE_END;
    private static double GRAPH_RANGE_BZ_INCREASED_START;
    private static double GRAPH_RANGE_BZ_INCREASED_END;
    private static double GRAPH_RANGE_BZ_HIGH_START;
    private static double GRAPH_RANGE_BZ_HIGH_END;
    private static double GRAPH_RANGE_BZ_EXTREME_START;
    private static double GRAPH_RANGE_BZ_EXTREME_END;

    /** Try to load and parse config.properties */
    public static void loadConfig() {
        FileInputStream config;
        Properties properties = new Properties();
        try {
            config = new FileInputStream("config/config.properties");
            properties.load(config);
        } catch (IOException e) {
            System.err.println("Error: config/config.properties is not exist.");
            System.exit(0);
        }
        try {
            DB_URL = properties.getProperty("db.host");
            USER = properties.getProperty("db.login");
            PASS = properties.getProperty("db.password");
            BOT_USERNAME = properties.getProperty("bot.username");
            BOT_TOKEN = properties.getProperty("bot.token");
            WEBSITE = properties.getProperty("bot.site");
            NOTIFY_INTERVAL = Integer.parseInt(properties.getProperty("notif.interval"));
            TOP_DENSITY = Double.parseDouble(properties.getProperty("notif.top.density"));
            TOP_SPEED = Double.parseDouble(properties.getProperty("notif.top.speed"));
            TOP_BZ = Double.parseDouble(properties.getProperty("notif.top.bz"));
            JSON_TO_DB_SYNC_ID = Integer.parseInt(properties.getProperty("bot.recovery"));
            MAG_5MIN = properties.getProperty("json.mag.5min");
            PLASM_5MIN = properties.getProperty("json.plasma.5min");
            MAG_2H = properties.getProperty("json.mag.2h");
            PLASM_2H = properties.getProperty("json.plasma.2h");
            MAG_24H = properties.getProperty("json.mag.24h");
            PLASM_24H = properties.getProperty("json.plasma.24h");
            MAG_7DAY = properties.getProperty("json.mag.7day");
            PLASM_7DAY = properties.getProperty("json.plasma.7day");
            GRAPH_COLOR_QUIET = properties.getProperty("graph.color.quiet");
            GRAPH_COLOR_MODERATE = properties.getProperty("graph.color.moderate");
            GRAPH_COLOR_INCREASED = properties.getProperty("graph.color.increased");
            GRAPH_COLOR_HIGH = properties.getProperty("graph.color.high");
            GRAPH_COLOR_EXTREME = properties.getProperty("graph.color.extreme");
            GRAPH_RANGE_DENS_QUIET_START = Integer.parseInt(properties.
                    getProperty("graph.range.density.quiet.start"));
            GRAPH_RANGE_DENS_QUIET_END = Integer.parseInt(properties.
                    getProperty("graph.range.density.quiet.end"));
            GRAPH_RANGE_DENS_MODERATE_START	= Integer.parseInt(properties.
                    getProperty("graph.range.density.moderate.start"));
            GRAPH_RANGE_DENS_MODERATE_END = Integer.parseInt(properties.
                    getProperty("graph.range.density.moderate.end"));
            GRAPH_RANGE_DENS_INCREASED_START = Integer.parseInt(properties.
                    getProperty("graph.range.density.increased.start"));
            GRAPH_RANGE_DENS_INCREASED_END = Integer.parseInt(properties.
                    getProperty("graph.range.density.increased.end"));
            GRAPH_RANGE_DENS_HIGH_START = Integer.parseInt(properties.
                    getProperty("graph.range.density.high.start"));
            GRAPH_RANGE_DENS_HIGH_END = Integer.parseInt(properties.
                    getProperty("graph.range.density.high.end"));
            GRAPH_RANGE_DENS_EXTREME_START = Integer.parseInt(properties.
                    getProperty("graph.range.density.extreme.start"));
            GRAPH_RANGE_DENS_EXTREME_END = Integer.parseInt(properties.
                    getProperty("graph.range.density.extreme.end"));
            GRAPH_RANGE_SPEED_QUIET_START = Integer.parseInt(properties.
                    getProperty("graph.range.speed.quiet.start"));
            GRAPH_RANGE_SPEED_QUIET_END	= Integer.parseInt(properties.
                    getProperty("graph.range.speed.quiet.end"));
            GRAPH_RANGE_SPEED_MODERATE_START = Integer.parseInt(properties.
                    getProperty("graph.range.speed.moderate.start"));
            GRAPH_RANGE_SPEED_MODERATE_END = Integer.parseInt(properties.
                    getProperty("graph.range.speed.moderate.end"));
            GRAPH_RANGE_SPEED_INCREASED_START = Integer.parseInt(properties.
                    getProperty("graph.range.speed.increased.start"));
            GRAPH_RANGE_SPEED_INCREASED_END	= Integer.parseInt(properties.
                    getProperty("graph.range.speed.increased.end"));
            GRAPH_RANGE_SPEED_HIGH_START = Integer.parseInt(properties.
                    getProperty("graph.range.speed.high.start"));
            GRAPH_RANGE_SPEED_HIGH_END = Integer.parseInt(properties.
                    getProperty("graph.range.speed.high.end"));
            GRAPH_RANGE_SPEED_EXTREME_START = Integer.parseInt(properties.
                    getProperty("graph.range.speed.extreme.start"));
            GRAPH_RANGE_SPEED_EXTREME_END = Integer.parseInt(properties.
                    getProperty("graph.range.speed.extreme.end"));
            GRAPH_RANGE_BZ_QUIET_START = Double.parseDouble(properties.
                    getProperty("graph.range.bz.quiet.start"));
            GRAPH_RANGE_BZ_QUIET_END = Double.parseDouble(properties.
                    getProperty("graph.range.bz.quiet.end"));
            GRAPH_RANGE_BZ_MODERATE_START = Double.parseDouble(properties.
                    getProperty("graph.range.bz.moderate.start"));
            GRAPH_RANGE_BZ_MODERATE_END	= Double.parseDouble(properties.
                    getProperty("graph.range.bz.moderate.end"));
            GRAPH_RANGE_BZ_INCREASED_START = Double.parseDouble(properties.
                    getProperty("graph.range.bz.increased.start"));
            GRAPH_RANGE_BZ_INCREASED_END = Double.parseDouble(properties.
                    getProperty("graph.range.bz.increased.end"));
            GRAPH_RANGE_BZ_HIGH_START = Double.parseDouble(properties.
                    getProperty("graph.range.bz.high.start"));
            GRAPH_RANGE_BZ_HIGH_END	= Double.parseDouble(properties.
                    getProperty("graph.range.bz.high.end"));
            GRAPH_RANGE_BZ_EXTREME_START = Double.parseDouble(properties.
                    getProperty("graph.range.bz.extreme.start"));
            GRAPH_RANGE_BZ_EXTREME_END = Double.parseDouble(properties.
                    getProperty("graph.range.bz.extreme.end"));
            setDbConnection();
        } catch (Exception e) {
            System.err.println("Error: seems like config.properties has wrong parameters. Please check " +
                    "config.properties file syntax and try again. \nStackTrace: " + e);
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
        notifScheduler.scheduleAtFixedRate(new Notification(), 60, 60, TimeUnit.SECONDS);
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

    public static String getGraphColorQuiet() {
        return GRAPH_COLOR_QUIET;
    }

    public static String getGraphColorModerate() {
        return GRAPH_COLOR_MODERATE;
    }

    public static String getGraphColorIncreased() {
        return GRAPH_COLOR_INCREASED;
    }

    public static String getGraphColorHigh() {
        return GRAPH_COLOR_HIGH;
    }

    public static String getGraphColorExtreme() {
        return GRAPH_COLOR_EXTREME;
    }

    public static double getTopDensity() {
        return TOP_DENSITY;
    }

    public static double getTopSpeed() {
        return TOP_SPEED;
    }

    public static double getTopBz() {
        return TOP_BZ;
    }

    public static int getGraphRangeDensQuietStart() {
        return GRAPH_RANGE_DENS_QUIET_START;
    }

    public static int getGraphRangeDensQuietEnd() {
        return GRAPH_RANGE_DENS_QUIET_END;
    }

    public static int getGraphRangeDensModerateStart() {
        return GRAPH_RANGE_DENS_MODERATE_START;
    }

    public static int getGraphRangeDensModerateEnd() {
        return GRAPH_RANGE_DENS_MODERATE_END;
    }

    public static int getGraphRangeDensIncreasedStart() {
        return GRAPH_RANGE_DENS_INCREASED_START;
    }

    public static int getGraphRangeDensIncreasedEnd() {
        return GRAPH_RANGE_DENS_INCREASED_END;
    }

    public static int getGraphRangeDensHighStart() {
        return GRAPH_RANGE_DENS_HIGH_START;
    }

    public static int getGraphRangeDensHighEnd() {
        return GRAPH_RANGE_DENS_HIGH_END;
    }

    public static int getGraphRangeDensExtremeStart() {
        return GRAPH_RANGE_DENS_EXTREME_START;
    }

    public static int getGraphRangeDensExtremeEnd() {
        return GRAPH_RANGE_DENS_EXTREME_END;
    }

    public static int getGraphRangeSpeedQuietStart() {
        return GRAPH_RANGE_SPEED_QUIET_START;
    }

    public static int getGraphRangeSpeedQuietEnd() {
        return GRAPH_RANGE_SPEED_QUIET_END;
    }

    public static int getGraphRangeSpeedModerateStart() {
        return GRAPH_RANGE_SPEED_MODERATE_START;
    }

    public static int getGraphRangeSpeedModerateEnd() {
        return GRAPH_RANGE_SPEED_MODERATE_END;
    }

    public static int getGraphRangeSpeedIncreasedStart() {
        return GRAPH_RANGE_SPEED_INCREASED_START;
    }

    public static int getGraphRangeSpeedIncreasedEnd() {
        return GRAPH_RANGE_SPEED_INCREASED_END;
    }

    public static int getGraphRangeSpeedHighStart() {
        return GRAPH_RANGE_SPEED_HIGH_START;
    }

    public static int getGraphRangeSpeedHighEnd() {
        return GRAPH_RANGE_SPEED_HIGH_END;
    }

    public static int getGraphRangeSpeedExtremeStart() {
        return GRAPH_RANGE_SPEED_EXTREME_START;
    }

    public static int getGraphRangeSpeedExtremeEnd() {
        return GRAPH_RANGE_SPEED_EXTREME_END;
    }

    public static double getGraphRangeBzQuietStart() {
        return GRAPH_RANGE_BZ_QUIET_START;
    }

    public static double getGraphRangeBzQuietEnd() {
        return GRAPH_RANGE_BZ_QUIET_END;
    }

    public static double getGraphRangeBzModerateStart() {
        return GRAPH_RANGE_BZ_MODERATE_START;
    }

    public static double getGraphRangeBzModerateEnd() {
        return GRAPH_RANGE_BZ_MODERATE_END;
    }

    public static double getGraphRangeBzIncreasedStart() {
        return GRAPH_RANGE_BZ_INCREASED_START;
    }

    public static double getGraphRangeBzIncreasedEnd() {
        return GRAPH_RANGE_BZ_INCREASED_END;
    }

    public static double getGraphRangeBzHighStart() {
        return GRAPH_RANGE_BZ_HIGH_START;
    }

    public static double getGraphRangeBzHighEnd() {
        return GRAPH_RANGE_BZ_HIGH_END;
    }

    public static double getGraphRangeBzExtremeStart() {
        return GRAPH_RANGE_BZ_EXTREME_START;
    }

    public static double getGraphRangeBzExtremeEnd() {
        return GRAPH_RANGE_BZ_EXTREME_END;
    }
}
