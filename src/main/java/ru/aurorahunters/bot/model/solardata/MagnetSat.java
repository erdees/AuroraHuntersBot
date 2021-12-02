package ru.aurorahunters.bot.model.solardata;

import java.sql.Timestamp;

public class MagnetSat {

    private final Timestamp timeStamp;
    private final double bzGsm;
    private final double btGsm;

    public MagnetSat(Timestamp timeStamp, double bzGsm, double btGsm) {
        this.timeStamp = timeStamp;
        this.bzGsm = bzGsm;
        this.btGsm = btGsm;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public double getBzGsm() {
        return bzGsm;
    }

    public double getBtGsm() {
        return btGsm;
    }

    @Override
    public String toString() {
        return "MagnetSat{" +
                "timeStamp=" + timeStamp +
                ", bzGsm=" + bzGsm +
                ", btGsm=" + btGsm +
                '}';
    }
}
