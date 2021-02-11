package ru.aurorahunters.bot.model.chart;

public class PreparedChart {

    private final Resolution resolution;

    public PreparedChart(Resolution resolution) {
        this.resolution = resolution;
    }

    public Resolution getResolution() {
        return resolution;
    }
}
