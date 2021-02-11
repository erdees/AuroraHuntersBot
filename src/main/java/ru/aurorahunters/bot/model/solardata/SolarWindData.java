package ru.aurorahunters.bot.model.solardata;

public class SolarWindData {

    private final double density;
    private final double speed;
    private final double bzGsm;

    public SolarWindData(double density, double speed, double bzGsm) {
        this.density = density;
        this.speed = speed;
        this.bzGsm = bzGsm;
    }

    public double getDensity() {
        return density;
    }

    public double getSpeed() {
        return speed;
    }

    public double getBzGsm() {
        return bzGsm;
    }
}
