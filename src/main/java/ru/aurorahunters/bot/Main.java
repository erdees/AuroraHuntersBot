package ru.aurorahunters.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.telegram.AuroraBot;
import ru.aurorahunters.bot.utils.GPSUtils;
import java.util.Locale;

import static java.lang.System.*;

public class Main {

    public static void main(String[] args) {
        Config.loadConfig();
        Locale.setDefault(new Locale("en", "UK"));
        GPSUtils.initializeZoneEngine();
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new AuroraBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        out.println("Bot started.");
        Config.initializeScheduler();
    }
}