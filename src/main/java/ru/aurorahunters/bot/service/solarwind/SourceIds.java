package ru.aurorahunters.bot.service.solarwind;

public enum SourceIds {
    DSCOVR(1),
    ACE(2);

    private final int id;

    SourceIds(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
