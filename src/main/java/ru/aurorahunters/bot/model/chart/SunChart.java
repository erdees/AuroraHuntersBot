package ru.aurorahunters.bot.model.chart;

import ru.aurorahunters.bot.enums.GraphTypeEnum;

public class SunChart extends PreparedChart {

    private final GraphTypeEnum graphTypeEnum;

    public SunChart(Resolution resolution, GraphTypeEnum graphTypeEnum) {
        super(resolution);
        this.graphTypeEnum = graphTypeEnum;
    }

    public GraphTypeEnum getGraphTypeEnum() {
        return graphTypeEnum;
    }
}
