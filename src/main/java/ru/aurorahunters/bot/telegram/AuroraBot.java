package ru.aurorahunters.bot.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.Config;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class AuroraBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                if (!SessionHandler.sessionHandler(update.getMessage().getText(),
                        update.getMessage().getChatId(), update.getMessage().getLocation()).isEmpty()) {
                    message = new SendMessage() // Create a SendMessage object with mandatory fields
                            .setChatId(update.getMessage().getChatId())
                            .setText(SessionHandler.sessionHandler(update.getMessage().getText(),
                                    update.getMessage().getChatId(), update.getMessage().getLocation()));
                    message.setParseMode(ParseMode.HTML);
                    try {
                        execute(message); // Call method to send the message
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ParseException | SQLException | IOException | TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if (update.getMessage().hasLocation()) {
            try {
                message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .setText(SessionHandler.sessionHandler("",
                                update.getMessage().getChatId(), update.getMessage().getLocation()));
                message.setParseMode(ParseMode.HTML);
            } catch (ParseException | SQLException | IOException | TelegramApiException e) {
                e.printStackTrace();
            }
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return Config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return Config.getBotToken();
    }
}