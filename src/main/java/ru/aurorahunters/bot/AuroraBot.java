package ru.aurorahunters.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

class AuroraBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        String answer = null;

        try {
            answer = botInteract(update);
        } catch (IOException | SQLException | ParseException | TelegramApiException e) {
            e.printStackTrace();
        }

        if (!answer.isEmpty()) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(answer); //responsible for message sending
            message.setParseMode(ParseMode.HTML);
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void log(Update update) {
        System.out.println(update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getText() + " " + update.getMessage().getChatId());
    }

    public String botInteract(Update update) throws IOException, SQLException, ParseException, TelegramApiException {
        String messageToSend = null;
        String msg = update.getMessage().getText();
        String name = update.getMessage().getFrom().getFirstName();
        Long chatID = update.getMessage().getChatId();
        messageToSend = SessionHandler.sessionHandler(msg, chatID);
        if (!messageToSend.isEmpty()) {
            System.out.println("Name: " + name + "; chat ID: " + chatID + "; message to send: " + messageToSend);
        }
        return messageToSend;
    }

    @Override
    public String getBotUsername() {
        //return "@usercabronbot"; //Test
        return "@aurorahunters_bot"; //Prod
    }

    @Override
    public String getBotToken() {
        //return "1166398193:AAFlkO9ie2gM-fx-M1erKZVY2Q7gh3T0SVI"; //Test
        return "1377530534:AAE-z4KAIQBZn64TcQ--qQmT8yk_JdMcDd8"; //Prod
    }
}