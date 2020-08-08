package ru.aurorahunters.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

class AuroraBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = null;
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
            SendMessage message = null;
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
        return "@usercabronbot"; //Test
        //return "@aurorahunters_bot"; //Prod
    }

    @Override
    public String getBotToken() {
        return "1166398193:AAFlkO9ie2gM-fx-M1erKZVY2Q7gh3T0SVI"; //Test
        //return "1377530534:AAE-z4KAIQBZn64TcQ--qQmT8yk_JdMcDd8"; //Prod
    }
}