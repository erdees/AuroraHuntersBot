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
        //log(update);
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
        if (SessionHandler.getMap().containsKey(chatID)) {
            if (msg.equals("/graph_all")) {
                sendImage(update, 4);
            }
            else if (msg.equals("/graph_bz")) {
                sendImage(update, 3);
            }
            else if (msg.equals("/graph_speed")) {
                sendImage(update, 2);
            }
            else if (msg.equals("/graph_density")) {
                sendImage(update, 1);
            }
        }
        messageToSend = SessionHandler.handleSession(msg, chatID);
        if (!messageToSend.isEmpty()) {
            System.out.println("Name: " + name + "; chat ID: " + chatID + "; message to send: " + messageToSend);
        }
        return messageToSend;
    }

    public void sendImage(Update update, int graphID) throws IOException, TelegramApiException, ParseException, SQLException {
        Long chatID = update.getMessage().getChatId();

        if (SessionHandler.getMap().containsKey(chatID)) {

            if (graphID == 4) {
                SendPhoto message = new SendPhoto()
                        .setPhoto("SomeText", new FileInputStream(TimeGraph.getBzGraph(SessionHandler.getBot(chatID).getTimeZone())))
                        .setChatId(update.getMessage().getChatId());
                this.execute(message);
                SendPhoto message1 = new SendPhoto()
                        .setPhoto("SomeText", new FileInputStream(TimeGraph.getSpeedGraph(SessionHandler.getBot(chatID).getTimeZone())))
                        .setChatId(update.getMessage().getChatId());
                this.execute(message1);
                SendPhoto message2 = new SendPhoto()
                        .setPhoto("SomeText", new FileInputStream(TimeGraph.getDensityGraph(SessionHandler.getBot(chatID).getTimeZone())))
                        .setChatId(update.getMessage().getChatId());
                this.execute(message2);
            }
            else if (graphID == 3) {
                SendPhoto message = new SendPhoto()
                        .setPhoto("SomeText", new FileInputStream(TimeGraph.getBzGraph(SessionHandler.getBot(chatID).getTimeZone())))
                        .setChatId(update.getMessage().getChatId());
                this.execute(message);
            }
            else if (graphID ==2) {
                SendPhoto message = new SendPhoto()
                        .setPhoto("SomeText", new FileInputStream(TimeGraph.getSpeedGraph(SessionHandler.getBot(chatID).getTimeZone())))
                        .setChatId(update.getMessage().getChatId());
                this.execute(message);
            }
            else if (graphID == 1) {
                SendPhoto message = new SendPhoto()
                        .setPhoto("SomeText", new FileInputStream(TimeGraph.getDensityGraph(SessionHandler.getBot(chatID).getTimeZone())))
                        .setChatId(update.getMessage().getChatId());
                this.execute(message);
            }
        } else {
            botIsNotStarted(update);
        }
    }

    public void botIsNotStarted(Update update) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(update.getMessage().getChatId())
                .setText("Bot is not started"); //responsible for message sending
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
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