package ru.aurorahunters.bot.model;

import ru.aurorahunters.bot.enums.GraphTypeEnum;

public class ActualSunChart extends SunChart {

    private final String timezone;

    public ActualSunChart(GraphTypeEnum graphTypeEnum, String timezone, Resolution resolution) {
        super(resolution, graphTypeEnum);
        this.timezone = timezone;
    }

    public String getTimezone() {
        return timezone;
    }
}
