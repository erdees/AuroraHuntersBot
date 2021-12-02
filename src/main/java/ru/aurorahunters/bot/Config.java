package ru.aurorahunters.bot;

import ru.aurorahunters.bot.graphbuilder.ChartPreLoader;
import ru.aurorahunters.bot.service.solarwind.SunWindService;
import ru.aurorahunters.bot.service.magnetometer.MagnetometerService;
import ru.aurorahunters.bot.enums.MagnetEnum;
import ru.aurorahunters.bot.notification.NotificationService;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.System.*;

public class Config {
    private static String DB_URL;
    private static String USER;
    private static String PASS;
    private static Connection CONNECTION;
    private static String BOT_USERNAME;
    private static String BOT_TOKEN;
    private static String WEBSITE;
    private static int NOTIFY_INTERVAL;
    private static long JSON_TO_DB_SYNC_ID;
    private static String MAG_5MIN;
    private static String PLASM_5MIN;
    private static String MAG_2H;
    private static String PLASM_2H;
    private static String MAG_24H;
    private static String PLASM_24H;
    private static String MAG_7DAY;
    private static String PLASM_7DAY;
    private static String MAGN_KEV_1H;
    private static String MAGN_OUJ_1H;
    private static String MAGN_HAN_1H;
    private static String MAGN_NUR_1H;
    private static String MAGN_KEV_24H;
    private static String MAGN_OUJ_24H;
    private static String MAGN_HAN_24H;
    private static String MAGN_NUR_24H;
    private static boolean MAGN_KEV_ENABLE;
    private static boolean MAGN_OUJ_ENABLE;
    private static boolean MAGN_HAN_ENABLE;
    private static boolean MAGN_NUR_ENABLE;
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
    private static double GRAPH_RANGE_BT_QUIET_START;
    private static double GRAPH_RANGE_BT_QUIET_END;
    private static double GRAPH_RANGE_BT_MODERATE_START;
    private static double GRAPH_RANGE_BT_MODERATE_END;
    private static double GRAPH_RANGE_BT_INCREASED_START;
    private static double GRAPH_RANGE_BT_INCREASED_END;
    private static double GRAPH_RANGE_BT_HIGH_START;
    private static double GRAPH_RANGE_BT_HIGH_END;
    private static double GRAPH_RANGE_BT_EXTREME_START;
    private static double GRAPH_RANGE_BT_EXTREME_END;
    private static boolean GRAPH_PRELOADER_ENABLE;
    private static String GRAPH_PRELOADER_TIMEZONE;
    private static String GRAPH_PRELOADER_FOLDER;

    /** Try to load and parse config.properties */
    public static void loadConfig() {
        FileInputStream config;
        Properties properties = new Properties();

        try {
            config = new FileInputStream("config/config.properties");
            properties.load(config);
        } catch (IOException e) {
            err.println("Error: config/config.properties is not exist.");
            exit(0);
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
            JSON_TO_DB_SYNC_ID = long.parseLong(properties.getProperty("bot.recovery"));
            MAG_5MIN = properties.getProperty("json.mag.5min");
            PLASM_5MIN = properties.getProperty("json.plasma.5min");
            MAG_2H = properties.getProperty("json.mag.2h");
            PLASM_2H = properties.getProperty("json.plasma.2h");
            MAG_24H = properties.getProperty("json.mag.24h");
            PLASM_24H = properties.getProperty("json.plasma.24h");
            MAG_7DAY = properties.getProperty("json.mag.7day");
            PLASM_7DAY = properties.getProperty("json.plasma.7day");
            MAGN_KEV_1H = properties.getProperty("source.magn.kev.1h");
            MAGN_OUJ_1H = properties.getProperty("source.magn.ouj.1h");
            MAGN_HAN_1H = properties.getProperty("source.magn.han.1h");
            MAGN_NUR_1H = properties.getProperty("source.magn.nur.1h");
            MAGN_KEV_24H = properties.getProperty("source.magn.kev.24h");
            MAGN_OUJ_24H = properties.getProperty("source.magn.ouj.24h");
            MAGN_HAN_24H = properties.getProperty("source.magn.han.24h");
            MAGN_NUR_24H = properties.getProperty("source.magn.nur.24h");
            MAGN_KEV_ENABLE = Boolean.parseBoolean(properties.getProperty("source.mahn.kev.enable"));
            MAGN_OUJ_ENABLE = Boolean.parseBoolean(properties.getProperty("source.mahn.ouj.enable"));
            MAGN_HAN_ENABLE = Boolean.parseBoolean(properties.getProperty("source.mahn.han.enable"));
            MAGN_NUR_ENABLE = Boolean.parseBoolean(properties.getProperty("source.mahn.nur.enable"));
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
            GRAPH_RANGE_BT_QUIET_START = Double.parseDouble(properties.
                    getProperty("graph.range.bt.quiet.start"));
            GRAPH_RANGE_BT_QUIET_END = Double.parseDouble(properties.
                    getProperty("graph.range.bt.quiet.end"));
            GRAPH_RANGE_BT_MODERATE_START = Double.parseDouble(properties.
                    getProperty("graph.range.bt.moderate.start"));
            GRAPH_RANGE_BT_MODERATE_END	= Double.parseDouble(properties.
                    getProperty("graph.range.bt.moderate.end"));
            GRAPH_RANGE_BT_INCREASED_START = Double.parseDouble(properties.
                    getProperty("graph.range.bt.increased.start"));
            GRAPH_RANGE_BT_INCREASED_END = Double.parseDouble(properties.
                    getProperty("graph.range.bt.increased.end"));
            GRAPH_RANGE_BT_HIGH_START = Double.parseDouble(properties.
                    getProperty("graph.range.bt.high.start"));
            GRAPH_RANGE_BT_HIGH_END	= Double.parseDouble(properties.
                    getProperty("graph.range.bt.high.end"));
            GRAPH_RANGE_BT_EXTREME_START = Double.parseDouble(properties.
                    getProperty("graph.range.bt.extreme.start"));
            GRAPH_RANGE_BT_EXTREME_END = Double.parseDouble(properties.
                    getProperty("graph.range.bt.extreme.end"));
            GRAPH_PRELOADER_ENABLE = Boolean.parseBoolean(properties.
                    getProperty("graph.preloader.enabled"));
            GRAPH_PRELOADER_TIMEZONE = properties.getProperty("graph.preloader.timezone");
            GRAPH_PRELOADER_FOLDER = properties.getProperty("graph.preloader.folder");
            setDbConnection();
        } catch (Exception e) {
            err.println("Error: seems like config.properties has wrong parameters. Please check " +
                    "config.properties file syntax and try again. \nStackTrace: " + e);
            exit(0);
        }
    }

    /** Try to establish a connection with the Database */
    private static void setDbConnection() {
        try {
            CONNECTION = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            err.println("Error: seems like PostgreSQL database is not available or its credentials is not correct.");
            exit(0);
        }
    }

    /** Scheduler settings */
    public static void initializeSchedulers() {
        configureSunWindServiceScheduler();
        configureNotificationScheduler();
        configureMagnetometerScheduler();
        configureChartPreloaderScheduler();
    }

    /** Create required Schedulers for Sun Wind fetch Service according to the config.properties
     * file */
    private static void configureSunWindServiceScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(new SunWindService(Config.getJsonToDbSyncId()), 0, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new SunWindService(1), 0, 40, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new SunWindService(2), 59, 60, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(new SunWindService(4), 48, 48, TimeUnit.HOURS);
    }

    /** Create required Schedulers for chart preloader according to the config.properties file */
    private static void configureNotificationScheduler() {
        ScheduledExecutorService notifScheduler = Executors.newSingleThreadScheduledExecutor();

        notifScheduler.scheduleAtFixedRate(new NotificationService(), 60, 60, TimeUnit.SECONDS);
    }

    /** Create required Schedulers for magnetometers according to the config.properties file */
    private static void configureMagnetometerScheduler() {
        ScheduledExecutorService magnetScheduler = Executors.newSingleThreadScheduledExecutor();

        if (MAGN_KEV_ENABLE) {
            magnetScheduler.scheduleAtFixedRate(new MagnetometerService(MagnetEnum.KEV, false),
                    10, 240, TimeUnit.SECONDS);
            magnetScheduler.scheduleAtFixedRate(new MagnetometerService(MagnetEnum.KEV, true),
                    5, 240, TimeUnit.MINUTES);
        }
        if (MAGN_OUJ_ENABLE) {
            magnetScheduler.scheduleAtFixedRate(new MagnetometerService(MagnetEnum.OUJ, false),
                    10, 240, TimeUnit.SECONDS);
            magnetScheduler.scheduleAtFixedRate(new MagnetometerService(MagnetEnum.OUJ, true),
                    5, 240, TimeUnit.MINUTES);
        }
        if (MAGN_HAN_ENABLE) {
            magnetScheduler.scheduleAtFixedRate(new MagnetometerService(MagnetEnum.HAN, false),
                    10, 240, TimeUnit.SECONDS);
            magnetScheduler.scheduleAtFixedRate(new MagnetometerService(MagnetEnum.HAN, true),
                    5, 240, TimeUnit.MINUTES);
        }
        if (MAGN_NUR_ENABLE) {
            magnetScheduler.scheduleAtFixedRate(new MagnetometerService(MagnetEnum.NUR, false),
                    10, 240, TimeUnit.SECONDS);
            magnetScheduler.scheduleAtFixedRate(new MagnetometerService(MagnetEnum.NUR, true),
                    5, 240, TimeUnit.MINUTES);
        }
    }

    /** Create required Schedulers for chart preloader according to the config.properties file */
    private static void configureChartPreloaderScheduler() {
        ScheduledExecutorService imagePreLoader = Executors.newSingleThreadScheduledExecutor();

        if (GRAPH_PRELOADER_ENABLE) {
            imagePreLoader.scheduleAtFixedRate(new ChartPreLoader(), 15, 50, TimeUnit.SECONDS);
        }
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

    public static long getJsonToDbSyncId() {
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

    public static String getMagnKev1h() {
        return MAGN_KEV_1H;
    }

    public static String getMagnOuj1h() {
        return MAGN_OUJ_1H;
    }

    public static String getMagnHan1h() {
        return MAGN_HAN_1H;
    }

    public static String getMagnNur1h() {
        return MAGN_NUR_1H;
    }

    public static String getMagnKev24h() {
        return MAGN_KEV_24H;
    }

    public static String getMagnOuj24h() {
        return MAGN_OUJ_24H;
    }

    public static String getMagnHan24h() {
        return MAGN_HAN_24H;
    }

    public static String getMagnNur24h() {
        return MAGN_NUR_24H;
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

    public static double getGraphRangeBtQuietStart() {
        return GRAPH_RANGE_BT_QUIET_START;
    }

    public static double getGraphRangeBtQuietEnd() {
        return GRAPH_RANGE_BT_QUIET_END;
    }

    public static double getGraphRangeBtModerateStart() {
        return GRAPH_RANGE_BT_MODERATE_START;
    }

    public static double getGraphRangeBtModerateEnd() {
        return GRAPH_RANGE_BT_MODERATE_END;
    }

    public static double getGraphRangeBtIncreasedStart() {
        return GRAPH_RANGE_BT_INCREASED_START;
    }

    public static double getGraphRangeBtIncreasedEnd() {
        return GRAPH_RANGE_BT_INCREASED_END;
    }

    public static double getGraphRangeBtHighStart() {
        return GRAPH_RANGE_BT_HIGH_START;
    }

    public static double getGraphRangeBtHighEnd() {
        return GRAPH_RANGE_BT_HIGH_END;
    }

    public static double getGraphRangeBtExtremeStart() {
        return GRAPH_RANGE_BT_EXTREME_START;
    }

    public static double getGraphRangeBtExtremeEnd() {
        return GRAPH_RANGE_BT_EXTREME_END;
    }

    public static boolean isGraphPreloaderEnable() {
        return GRAPH_PRELOADER_ENABLE;
    }

    public static String getGraphPreloaderTimezone() {
        return GRAPH_PRELOADER_TIMEZONE;
    }

    public static String getGraphPreloaderFolder() {
        return GRAPH_PRELOADER_FOLDER;
    }
}
