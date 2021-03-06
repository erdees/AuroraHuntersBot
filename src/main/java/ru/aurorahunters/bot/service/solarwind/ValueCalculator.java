package ru.aurorahunters.bot.service.solarwind;

import ru.aurorahunters.bot.dao.DataDAO;
import java.sql.SQLException;

public class ValueCalculator {

    /** Calculation of solar wind arrival time to the Earth. */
    public String getWaitingTime() throws SQLException {
        double speed = new DataDAO().getLastSpeed();
        double calc = 1500000.0 / 60.0 / 60.0 / speed;
        double result = (Math.floor(calc * 100) / 100) * 60;
        int hours = (int) result / 60; //since both are ints, you get an int
        int minutes = (int) result % 60;
        return hours + "h " + minutes + "m";
    }
}
