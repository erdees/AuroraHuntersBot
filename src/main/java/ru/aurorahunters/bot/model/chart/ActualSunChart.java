package ru.aurorahunters.bot.model.chart;

import ru.aurorahunters.bot.graphbuilder.GraphTypeEnum;

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
