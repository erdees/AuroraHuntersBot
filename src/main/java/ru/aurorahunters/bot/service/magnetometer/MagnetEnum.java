package ru.aurorahunters.bot.service.magnetometer;

import ru.aurorahunters.bot.Config;

public enum MagnetEnum {
    KEV("magnetometer_kev", "Kevo (KEV)",
            Config.getMagnKev1h(), Config.getMagnKev24h()),
    OUJ("magnetometer_ouj", "Oulujärvi (OUJ)",
            Config.getMagnOuj1h(), Config.getMagnOuj24h()),
    HAN("magnetometer_han", "Hankasalmi (HAN)",
            Config.getMagnHan1h(),Config.getMagnHan24h()),
    NUR("magnetometer_nur", "Nurmijärvi (NUR)",
            Config.getMagnNur1h(),Config.getMagnNur24h());

    String dbTableName;
    String printName;
    String latestDataUrl;
    String dailyDataUrl;

    MagnetEnum(String dbTableName, String printName, String latestDataUrl, String dailyDataUrl) {
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
