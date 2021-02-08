package ru.aurorahunters.bot.model;

import ru.aurorahunters.bot.enums.MagnetEnum;

public class MagnetChart extends PreparedChart {

    private final MagnetEnum magnetEnum;
    private final String timezone;

    public MagnetChart(MagnetEnum magnetEnum, String timezone, Resolution resolution) {
        super(resolution);
        this.magnetEnum = magnetEnum;
        this.timezone = timezone;
    }

    public MagnetEnum getMagnetEnum() {
        return magnetEnum;
    }

    public String getTimezone() {
        return timezone;
    }
}
