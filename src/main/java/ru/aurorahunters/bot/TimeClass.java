package ru.aurorahunters.bot;

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

    public static void initializeZoneEngine() {
        engine = TimeZoneEngine.initialize();
    }

    public static String getGpsTimezone(Location location) {
        double v = location.getLatitude();
        double v1 = location.getLongitude();
        List<ZoneId> list = engine.queryAll(v, v1);
        ZoneId id = list.get(0);
        Instant instant = Instant.now();
        return id.getRules().getOffset(instant).toString();
    }

    public static String getCurrentTime() throws ParseException {
        String fromDateString = new Date().toString();
        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date fromDate = formatter.parse(fromDateString);
        TimeZone central = TimeZone.getTimeZone("UTC+03:00");
        formatter.setTimeZone(central);
        return formatter.format(fromDate);
    }
}
