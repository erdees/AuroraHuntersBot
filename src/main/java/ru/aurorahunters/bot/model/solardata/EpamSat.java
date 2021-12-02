package ru.aurorahunters.bot.model.solardata;

import java.sql.Timestamp;

public class EpamSat {

    private final Timestamp timeStamp;
    private final double density;
    private final double speed;

    public EpamSat(Timestamp timeStamp, double density, double speed) {
        this.timeStamp = timeStamp;
        this.density = density;
        this.speed = speed;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public double getDensity() {
        return density;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "EpamSat{" +
                "timeStamp=" + timeStamp +
                ", density=" + density +
                ", speed=" + speed +
                '}';
    }
}
