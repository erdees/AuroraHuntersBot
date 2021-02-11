package ru.aurorahunters.bot.model.solardata;

public class PlasmData {

    private final String timestamp;
    private final double density;
    private final double speed;

    public PlasmData(String timestamp, double density, double speed) {
        this.timestamp = timestamp;
        this.density = density;
        this.speed = speed;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getDensity() {
        return density;
    }

    public double getSpeed() {
        return speed;
    }
}
