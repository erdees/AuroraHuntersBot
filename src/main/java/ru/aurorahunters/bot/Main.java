package ru.aurorahunters.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.controller.JsonToDB;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        Locale.setDefault(new Locale("en", "RU"));
        Config.loadConfig();
        TimeClass.initializeZoneEngine();
        System.out.println("Bot started.");

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService notifScheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(new JsonToDB(3), 0, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new JsonToDB(1), 0, 40, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new JsonToDB(2), 59, 60, TimeUnit.MINUTES);

        notifScheduler.scheduleAtFixedRate(new Notification(), 10, 60, TimeUnit.SECONDS);

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new AuroraBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}