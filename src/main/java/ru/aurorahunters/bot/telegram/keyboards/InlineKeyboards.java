package ru.aurorahunters.bot.telegram.keyboards;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.dao.SessionsDAO;
import ru.aurorahunters.bot.telegram.AuroraBot;
import ru.aurorahunters.bot.telegram.MessageSender;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InlineKeyboards {

    public void notificationMessage(Long chatId, String notifMassage) throws TelegramApiException, SQLException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();

        if (new SessionsDAO().getUserNotif(chatId))
        {
            inlineKeyboardButton1.setText("Disable notifications");
            inlineKeyboardButton1.setCallbackData("/disablenotif");
        } else {
            inlineKeyboardButton1.setText("Enable notifications");
            inlineKeyboardButton1.setCallbackData("/enablenotif");
        }

        inlineKeyboardButton2.setText("See latest charts");
        inlineKeyboardButton2.setCallbackData("/latestcharts");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        new MessageSender(chatId).sendMessage(notifMassage, inlineKeyboardMarkup);
    }

    public void updateNotifInlineKeyboard(Update update, String text, String command) {
        AuroraBot handler = new AuroraBot();

        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String inlineMessageId = update.getCallbackQuery().getInlineMessageId();

        EditMessageReplyMarkup newMessage = new EditMessageReplyMarkup();
        newMessage.setChatId(String.valueOf(chatId));
        newMessage.setMessageId(messageId);
        newMessage.setInlineMessageId(inlineMessageId);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText(text);
        inlineKeyboardButton1.setCallbackData(command);

        inlineKeyboardButton2.setText("See latest charts");
        inlineKeyboardButton2.setCallbackData("/latestcharts");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        newMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            handler.execute(newMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
