package ru.aurorahunters.bot.service.magnetometer;

import ru.aurorahunters.bot.enums.MagnetEnum;
import ru.aurorahunters.bot.dao.MagnetometerDAO;

import java.io.IOException;
import java.sql.*;

public class MagnetometerService implements Runnable {

    private final MagnetEnum type;
    private final boolean isDailyData;

    public MagnetometerService(MagnetEnum type, boolean isDailyData) {
        this.type = type;
        this.isDailyData = isDailyData;
    }

    /** Requests a magnetometer data according to a condition (1 hour or daily)
     * in a service mode from Scheduler. */
    @Override
    public void run() {
        try {
            if (isDailyData) {
                new MagnetometerDAO().insertValues
                        (new MagnetometerFetcher(type).getDailyData(), type);
            } else  {
                new MagnetometerDAO().insertValues
                        (new MagnetometerFetcher(type).getLatestData(), type);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}


