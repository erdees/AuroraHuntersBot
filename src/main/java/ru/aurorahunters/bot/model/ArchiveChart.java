package ru.aurorahunters.bot.model;

import ru.aurorahunters.bot.enums.GraphTypeEnum;

public class ArchiveChart extends SunChart {

    private final String date;

    public ArchiveChart(GraphTypeEnum graphTypeEnum, Resolution resolution,  String date) {
        super(resolution, graphTypeEnum);
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
