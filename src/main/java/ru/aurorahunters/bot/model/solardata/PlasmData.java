package ru.aurorahunters.bot.model.solardata;

import java.sql.Timestamp;

public class PlasmData {

    private final Timestamp timestamp;
    private final double density;
    private final double speed;

    public PlasmData(Timestamp timestamp, double density, double speed) {
        this.timestamp = timestamp;
        this.density = density;
        this.speed = speed;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public double getDensity() {
        return density;
    }

    public double getSpeed() {
        return speed;
    }
}
