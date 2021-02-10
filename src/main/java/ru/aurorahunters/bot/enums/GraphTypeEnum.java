package ru.aurorahunters.bot.enums;

public enum GraphTypeEnum {
    DENSITY("density", "Proton Density [p/cc]"),
    SPEED("speed", "Bulk Speed [km/s]"),
    BZ_GSM("bz_gsm", "Bz [nT]"),
    BT("bt", "Bt [nT]"),
    DENSITY_H("density", "Proton Density [p/cc]"),
    SPEED_H("speed", "Bulk Speed [km/s]"),
    BZ_GSM_H("bz_gsm", "Bz [nT]");

    String dbKey;
    String printName;

    GraphTypeEnum(String dbKey, String printName) {
        this.dbKey = dbKey;
        this.printName = printName;
    }

    public String getDbKey() {
        return dbKey;
    }

    public String getPrintName() {
        return printName;
    }
}

