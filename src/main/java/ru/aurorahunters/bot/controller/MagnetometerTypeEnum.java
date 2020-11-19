package ru.aurorahunters.bot.controller;

import ru.aurorahunters.bot.Config;

public enum MagnetometerTypeEnum {
    KEV("magnetometer_kev", "Kevo (KEV)",
            Config.getMagnKev1h(), Config.getMagnKev24h()),
    OUJ("magnetometer_ouj", "Oulujärvi (OUJ)",
            Config.getMagnOuj1h(), Config.getMagnOuj24h()),
    HAN("magnetometer_han", "Hankasalmi (HAN)",
            Config.getMagnHan1h(),Config.getMagnHan24h()),
    NUR("magnetometer_nur", "Nurmijärvi Geophysical Observatory (NUR)",
            Config.getMagnNur1h(),Config.getMagnNur24h());

    String dbTableName;
    String printName;
    String latestDataUrl;
    String dailyDataUrl;

    MagnetometerTypeEnum(String dbTableName, String printName, String latestDataUrl, String dailyDataUrl) {
        this.dbTableName = dbTableName;
        this.printName = printName;
        this.latestDataUrl = latestDataUrl;
        this.dailyDataUrl = dailyDataUrl;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public String getPrintName() {
        return printName;
    }

    public String getLatestDataUrl() {
        return latestDataUrl;
    }

    public String getDailyDataUrl() {
        return dailyDataUrl;
    }
}
