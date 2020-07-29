package ru.aurorahunters.bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

public class SessionHandler {
    private static HashMap<Long, MessageHandler>  map = new HashMap<>();
    private static MessageHandler bot;

    public static String handleSession(String input, Long chatID) throws IOException, SQLException, ParseException,
            TelegramApiException {
        if(!map.containsKey(chatID)){
            if (input.contains("/start") || input.equals("/start@aurorahunters_bot")) {
                bot = new MessageHandler(chatID);
                map.put(chatID,bot);
                return bot.setBotStarted();
            }
            else return new MessageHandler(chatID).respondMessage(input);
        } else {
            bot = map.get(chatID);
            if (input.equals("/stop") || input.equals("/stop@aurorahunters_bot")) {
                map.remove(chatID);
                return "Bot stopped.\n";
            }
        }
        return bot.respondMessage(input);
    }

    public static HashMap<Long, MessageHandler> getMap() {
        return map;
    }
}
