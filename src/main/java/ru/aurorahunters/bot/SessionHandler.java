package ru.aurorahunters.bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

public class SessionHandler {
    private static HashMap<Long, MessageHandler>  map = new HashMap<>();
    private static MessageHandler bot;

    public static String handleSession(String input, Long chatID) throws IOException, SQLException, ParseException, TelegramApiException {
        if(!map.containsKey(chatID)){
            if (input.contains("/start") || input.equals("/start@aurorahunters_bot")) {
                bot = new MessageHandler(chatID);
                map.put(chatID,bot);
                bot.setBotStarted();
                return bot.run(input);
            }
            else if (input.contains("/stop") || input.equals("/stop@aurorahunters_bot")) {
                return "The bot is not running. Please type /start to initialize it.";
            }
            else if (input.contains("/stat") || input.equals("/stat@aurorahunters_bot") || input.equals("/next")
                    ||  input.equals("/next@aurorahunters_bot") || input.equals("/word") ||  input.equals("/word@aurorahunters_bot")) {
                return "The bot should be started";
            }
            else return "";
        } else {
            bot = map.get(chatID);
            if (input.equals("/stop") || input.equals("/stop@aurorahunters_bot")) {
                map.remove(chatID);
                return "Bot stopped.\n";
            }
        }
        return bot.run(input);
    }

    public static MessageHandler getBot(Long chatID) {
        if (map.containsKey(chatID)) {
            bot = map.get(chatID);
        }
        return bot;
    }

    public static HashMap<Long, MessageHandler> getMap() {
        return map;
    }
}
