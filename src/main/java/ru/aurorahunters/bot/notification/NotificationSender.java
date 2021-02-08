package ru.aurorahunters.bot.notification;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.dao.SessionsDAO;
import ru.aurorahunters.bot.telegram.AuroraBot;
import java.sql.SQLException;
import static java.lang.System.*;

public class NotificationSender {

    /**
     * Send a notification message to all bot users who subscribed to notifications.
     * @param message message which should be sent.
     */
    public void sendNotif(String message) throws SQLException, InterruptedException {
        long currentChatId;
        for (Long chatId : new SessionsDAO().getSubscribersList()) {
            currentChatId = chatId;
            AuroraBot notificationEvent = new AuroraBot();
            SendMessage notificationMessage =
                    new SendMessage(currentChatId, message).setParseMode(ParseMode.HTML);
            try {
                notificationEvent.execute(notificationMessage);
            } catch (TelegramApiException e) {
                out.println("Caught exception while send broadcast messages. StackTrace:\n" + e);
                disableFailed(currentChatId);
            }
            Thread.sleep(35);
        }
    }

    private void disableFailed(long chatId) throws SQLException {
        new SessionsDAO().setDbNotif(false, chatId);
    }
}
