package ru.aurorahunters.bot.model.solardata;

import java.sql.Timestamp;

public class MagData {

    private final Timestamp timestamp;
    private final double bzGsm;
    private final double bt;

    public MagData(Timestamp timestamp, double bzGsm, double bt) {
        this.timestamp = timestamp;
        this.bzGsm = bzGsm;
        this.bt = bt;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public double getBzGsm() {
        return bzGsm;
    }

    public double getBt() {
        return bt;
    }
}
