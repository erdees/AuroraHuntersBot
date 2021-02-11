package ru.aurorahunters.bot.model.solardata;

public class FullSolarWindData extends SolarWindData {

    private final double bt;

    public FullSolarWindData(double density, double speed, double bzGsm, double bt) {
        super(density, speed, bzGsm);
        this.bt = bt;
    }

    public double getBt() {
        return bt;
    }
}
