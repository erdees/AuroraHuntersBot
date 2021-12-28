package ru.aurorahunters.bot.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class ImageSender {

    private final Long chatId;

    public ImageSender(long chatId) {
        this.chatId = chatId;
    }

    /**
     * Method which sends generated graph to user.
     * @param image is a generated and retrieved as a .png file TimeGraph image chart.
     */
    public void sendImage(File image) throws TelegramApiException {
        AuroraBot sendGraph = new AuroraBot();
        SendPhoto graph = new SendPhoto();
        graph.setPhoto(new InputFile(image));
        graph.setChatId(chatId.toString());
        sendGraph.execute(graph);
    }
}
