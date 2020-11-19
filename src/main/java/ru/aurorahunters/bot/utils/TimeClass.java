package ru.aurorahunters.bot.utils;

import net.iakovlev.timeshape.TimeZoneEngine;
import org.telegram.telegrambots.meta.api.objects.Location;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeClass {
    private static TimeZoneEngine engine;

    /** Initialize TimeZoneEngine library */
    public static void initializeZoneEngine() {
        engine = TimeZoneEngine.initialize();
    }

    /**
     * Method which convert received Location to location's timezone.
     * @param location is a Telegram object which contains latitude and longitude.
     * @return timezone in String format
     */
    public static String getGpsTimezone(Location location) {
        double v = location.getLatitude();
        double v1 = location.getLongitude();
        List<ZoneId> list = engine.queryAll(v, v1);
        ZoneId id = list.get(0);
        Instant instant = Instant.now();
        return id.getRules().getOffset(instant).toString();
    }

    /**
     * Method which retrieves GMT/UTC+00:00 Date and time according to local time and @formatter
     * @return GMT/UTC+00:00 Date
     */
    public static String GetCurrentGmtTime() throws ParseException {
        String fromDateString = new Date().toString();
        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date fromDate = formatter.parse(fromDateString);
        TimeZone central = TimeZone.getTimeZone("UTC+00:00");
        formatter.setTimeZone(central);
        return formatter.format(fromDate);
    }
}
