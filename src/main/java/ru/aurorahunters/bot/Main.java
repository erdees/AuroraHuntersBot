package ru.aurorahunters.bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.aurorahunters.bot.telegram.AuroraBot;
import ru.aurorahunters.bot.utils.GPSUtils;
import java.util.Locale;
import static java.lang.System.*;

public class Main {

    public static void main(String[] args) throws TelegramApiException {
        Config.loadConfig();
        Locale.setDefault(new Locale("en", "UK"));
        GPSUtils.initializeZoneEngine();
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(new AuroraBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        out.println("Bot started.");
        Config.initializeSchedulers();
    }
}