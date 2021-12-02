package ru.aurorahunters.bot.telegram.keyboards;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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

public class SettingsKeyboard {

    private final int DSCOVR = 1;
    private final int ACE = 2;

    public void settingsMessage(Long chatId) throws TelegramApiException {
        String notifMassage = "Please select required section:";
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Notification settings");
        inlineKeyboardButton1.setCallbackData("/notif_setting");

        inlineKeyboardButton2.setText("Choose satellite source");
        inlineKeyboardButton2.setCallbackData("/satellite_setting");

        inlineKeyboardButton3.setText("Configure Timezone");
        inlineKeyboardButton3.setCallbackData("/timezone_setting");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);
        new MessageSender(chatId).sendMessage(notifMassage, inlineKeyboardMarkup);
    }

    public void returnToSettings(Update update) {
        AuroraBot handler = new AuroraBot();
        String text = "Please select required section:";
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText newText = new EditMessageText();
        newText.setChatId(String.valueOf(chatId));
        newText.setMessageId(messageId);
        newText.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Notification settings");
        inlineKeyboardButton1.setCallbackData("/notif_setting");

        inlineKeyboardButton2.setText("Choose satellite source");
        inlineKeyboardButton2.setCallbackData("/satellite_setting");

        inlineKeyboardButton3.setText("Configure Timezone");
        inlineKeyboardButton3.setCallbackData("/timezone_setting");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);

        newText.setReplyMarkup(inlineKeyboardMarkup);

        try {
            handler.execute(newText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void notificationSettings(Update update, String buttonText, String command) throws SQLException {
        AuroraBot handler = new AuroraBot();
        String text = "Notification settings";
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText newText = new EditMessageText();
        newText.setChatId(String.valueOf(chatId));
        newText.setMessageId(messageId);
        newText.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();


        if (buttonText != null && command != null) {
            inlineKeyboardButton1.setText(buttonText);
            inlineKeyboardButton1.setCallbackData(command);

        } else {
            if (new SessionsDAO().getUserNotif(chatId))
            {
                inlineKeyboardButton1.setText("Notifications Enabled");
                inlineKeyboardButton1.setCallbackData("/settings_notif_off");
            } else {
                inlineKeyboardButton1.setText("Notifications Disabled");
                inlineKeyboardButton1.setCallbackData("/settings_notif_on");
            }
        }

        inlineKeyboardButton3.setText("<< Back to settings");
        inlineKeyboardButton3.setCallbackData("/settings_main");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);

        newText.setReplyMarkup(inlineKeyboardMarkup);

        try {
            handler.execute(newText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void timezoneSettings(Update update) {
        AuroraBot handler = new AuroraBot();
        String text = "Just send me your GPS location ";
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText newText = new EditMessageText();
        newText.setChatId(String.valueOf(chatId));
        newText.setMessageId(messageId);
        newText.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("<< Back to settings");
        inlineKeyboardButton1.setCallbackData("/settings_main");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        newText.setReplyMarkup(inlineKeyboardMarkup);

        try {
            handler.execute(newText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void satelliteSourceSettings(Update update, String buttonText, String command) throws SQLException {
        AuroraBot handler = new AuroraBot();
        String text = "Please choose satellite source";
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText newText = new EditMessageText();
        newText.setChatId(String.valueOf(chatId));
        newText.setMessageId(messageId);
        newText.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();

        if (buttonText != null && command != null) {
            inlineKeyboardButton1.setText(buttonText);
            inlineKeyboardButton1.setCallbackData(command);

        } else {
            if (new SessionsDAO().getSatelliteSource(chatId) == DSCOVR) {
                inlineKeyboardButton1.setText("NOAA DSCOVR");
                inlineKeyboardButton1.setCallbackData("/noaa_ace");
            }
            if (new SessionsDAO().getSatelliteSource(chatId) == ACE) {
                inlineKeyboardButton1.setText("NOAA ACE");
                inlineKeyboardButton1.setCallbackData("/noaa_dscovr");
            }
        }

        inlineKeyboardButton3.setText("<< Back to settings");
        inlineKeyboardButton3.setCallbackData("/settings_main");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);

        newText.setReplyMarkup(inlineKeyboardMarkup);

        try {
            handler.execute(newText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
