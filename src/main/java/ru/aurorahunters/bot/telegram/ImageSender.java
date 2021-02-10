package ru.aurorahunters.bot.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageSender {

    private final long chatId;

    public ImageSender(long chatId) {
        this.chatId = chatId;
    }

    /**
     * Method which sends generated graph to user.
     * @param image is a generated and retrieved as a .png file TimeGraph image chart.
     */
    public void sendImage(File image) throws FileNotFoundException, TelegramApiException {
        AuroraBot sendGraph = new AuroraBot();
        SendPhoto graph = new SendPhoto()
                .setPhoto("AuroraHuntersBot_Graph", new FileInputStream(image))
                .setChatId(chatId);
        sendGraph.execute(graph);
    }
}
