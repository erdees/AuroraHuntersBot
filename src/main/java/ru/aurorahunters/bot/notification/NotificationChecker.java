package ru.aurorahunters.bot.notification;

import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.model.SolarWindData;
import java.util.List;

public class NotificationChecker {

    private static final double TOP_DENSITY = Config.getTopDensity();
    private static final double TOP_SPEED = Config.getTopSpeed();
    private static final double TOP_BZ = Config.getTopBz();

    /** Check if one, two, three or none of parameters is true. */
    public boolean checkNotification(List<SolarWindData> lastFiveMin) {
        return isDensityHigh(lastFiveMin) || isSpeedHigh(lastFiveMin) || isBzHigh(lastFiveMin);
    }

    /**
     * Method which compares values from ArrayList, and if 5 of them is more or equals topDensity,
     * true will returned.
     * @return boolean with a result of check.
     */
    private boolean isDensityHigh(List<SolarWindData> lastFiveMin) {
        int count = 0;

        for (SolarWindData d : lastFiveMin) {
            if (d.getDensity() > TOP_DENSITY) {
                count++;
            }
        }
        return count >= 5;
    }

    /**
     * Method which compares values from ArrayList, and if 5 of them is more or equals topSpeed,
     * true will returned.
     * @return boolean with a result of check.
     */
    private boolean isSpeedHigh(List<SolarWindData> lastFiveMin) {
        int count = 0;

        for (SolarWindData d : lastFiveMin) {
            if (d.getSpeed() > TOP_SPEED) {
                count++;
            }
        }
        return count >= 5;
    }

    /**
     * Method which compares values from ArrayList, and if 5 of them is more or equals topBz,
     * true will returned.
     * @return boolean with a result of check.
     */
    private boolean isBzHigh(List<SolarWindData> lastFiveMin) {
        int count = 0;

        for (SolarWindData d : lastFiveMin) {
            if (d.getBzGsm() <= TOP_BZ) {
                count++;
            }
        }
        return count >= 5;
    }
}
