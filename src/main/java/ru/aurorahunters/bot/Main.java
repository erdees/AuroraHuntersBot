package ru.aurorahunters.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.controller.JsonToDB;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Config.loadConfig();
        Locale.setDefault(new Locale("en", "UK"));
        TimeClass.initializeZoneEngine();
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new AuroraBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        System.out.println("Bot started.");

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService notifScheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(new JsonToDB(Config.getBotDbRecovery()), 0, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new JsonToDB(1), 0, 40, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new JsonToDB(2), 59, 60, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(new JsonToDB(4), 48, 48, TimeUnit.HOURS);
        notifScheduler.scheduleAtFixedRate(new Notification(), 30, 60, TimeUnit.SECONDS);
    }
}