package ru.aurorahunters.bot.telegram;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MessageSender {

    private final long chatId;

    public MessageSender(long chatId) {
        this.chatId = chatId;
    }

    /**
     * Sends generated graph to user.
     * @param image is a generated and retrieved as a .png file TimeGraph image chart.
     */
    public void sendImage(File image) throws FileNotFoundException, TelegramApiException {
        AuroraBot sendGraph = new AuroraBot();
        SendPhoto graph = new SendPhoto()
                .setPhoto("AuroraHuntersBot_Graph", new FileInputStream(image))
                .setChatId(chatId);
        sendGraph.execute(graph);
    }

    public void sendMessage(String message, InlineKeyboardMarkup m) throws TelegramApiException {
        AuroraBot sendMessage = new AuroraBot();
        SendMessage messageObject = new SendMessage();
        messageObject.setChatId(chatId);
        messageObject.setText(message);
        messageObject.setReplyMarkup(m);
        messageObject.setParseMode(ParseMode.HTML);
        sendMessage.execute(messageObject);
    }
}
