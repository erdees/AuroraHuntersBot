package ru.aurorahunters.bot.model.solardata;

public class MagData {

    private final String timestamp;
    private final double bzGsm;
    private final double bt;

    public MagData(String timestamp, double bzGsm, double bt) {
        this.timestamp = timestamp;
        this.bzGsm = bzGsm;
        this.bt = bt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getBzGsm() {
        return bzGsm;
    }

    public double getBt() {
        return bt;
    }
}
