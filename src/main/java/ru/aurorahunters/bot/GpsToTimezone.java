package ru.aurorahunters.bot;

import net.iakovlev.timeshape.TimeZoneEngine;
import org.telegram.telegrambots.meta.api.objects.Location;
import java.time.*;
import java.util.List;

public class GpsToTimezone {
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
}
