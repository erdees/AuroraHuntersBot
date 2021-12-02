package ru.aurorahunters.bot.telegram;

import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.dao.SessionsDAO;
import ru.aurorahunters.bot.utils.GPSUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class SessionHandler {

    /**
     * Method which create, delete and hanlde user sessions.
     * @param input incoming to the Bot text message which should be processed.
     * @param chatId which is Telegram unique chat identifier.
     * @param gps Location object (optional) to configure a target chat timezone according
     * to a given Location.
     * @return String with a message which will be sent to a target chat.
     */
    public String sessionHandler(String input, Long chatId, Location gps)
            throws ParseException, SQLException, IOException, TelegramApiException {
        if (!new SessionsDAO().isChatIdExist(chatId)) {

            return createNewSession(input, chatId);
        }
        else {
            if (input.equals("/stop") || input.equals("/stop" + Config.getBotUsername())) {
                new SessionsDAO().deleteUserById(chatId);

                return "Bot stopped.";
            }
            else if (gps != null) {
                return new SessionsDAO().getCurrentChat(chatId).setGpsTimezone(
                        new GPSUtils().getGpsTimezone(gps));
            }
        }

        return new SessionsDAO().getCurrentChat(chatId).respondMessage(input);
    }

    private String createNewSession(String input, Long chatId) throws SQLException, ParseException, IOException, TelegramApiException {
        if (input.equals("/start") || input.equals("/start" + Config.getBotUsername())) {
            new SessionsDAO().insertNewUser(chatId);

            return new SessionsDAO().getCurrentChat(chatId).setBotStarted();
        }

        else return new MessageHandler(chatId).respondMessage(input);
    }
}