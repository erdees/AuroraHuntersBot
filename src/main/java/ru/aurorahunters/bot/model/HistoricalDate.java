package ru.aurorahunters.bot.model;

public class HistoricalDate {

    private final String initialDate;
    private final String endDate;

    public HistoricalDate(String initialDate, String endDate) {
        this.initialDate = initialDate;
        this.endDate = endDate;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
