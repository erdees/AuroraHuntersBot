package ru.aurorahunters.bot.service.solarwind.ace;

import ru.aurorahunters.bot.dao.DSCOVRDataDAO;
import ru.aurorahunters.bot.service.solarwind.SourceIds;

import java.io.IOException;
import java.sql.SQLException;

public class ACEService implements Runnable{

    @Override
    public void run() {
        try {
            new DSCOVRDataDAO(SourceIds.ACE.getId()).insertResults(new ACEFetcher().getLatestData());
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }
}
