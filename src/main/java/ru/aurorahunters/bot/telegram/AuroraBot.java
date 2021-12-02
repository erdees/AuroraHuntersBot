package ru.aurorahunters.bot.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.dao.SessionsDAO;
import ru.aurorahunters.bot.telegram.keyboards.InlineKeyboards;
import ru.aurorahunters.bot.telegram.keyboards.SettingsKeyboard;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class AuroraBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                if (!new SessionHandler().sessionHandler(update.getMessage().getText(),
                        update.getMessage().getChatId(), update.getMessage().getLocation()).isEmpty()) {
                    message = new SendMessage() // Create a SendMessage object with mandatory fields
                            .setChatId(update.getMessage().getChatId())
                            .setText(new SessionHandler().sessionHandler(update.getMessage().getText(),
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
        else if (update.hasCallbackQuery()) {
            try {
                processCalBackQueries(update);
            } catch (TelegramApiException | SQLException e) {
                e.printStackTrace();
            }
        }
        else if (update.getMessage().hasLocation()) {
            try {
                message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .setText(new SessionHandler().sessionHandler("",
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

    private void processCalBackQueries(Update update) throws TelegramApiException, SQLException {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (update.getCallbackQuery().getData().equals("/disablenotif")) {
            try {
                new SessionsDAO().getCurrentChat(chatId).setNotif(false);
                new InlineKeyboards().updateNotifInlineKeyboard(
                        update, "Enable notifications", "/enablenotif");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (update.getCallbackQuery().getData().equals("/enablenotif")) {
            try {
                new SessionsDAO().getCurrentChat(chatId).setNotif(true);
                new InlineKeyboards().updateNotifInlineKeyboard(
                        update, "Disable notifications", "/disablenotif");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (update.getCallbackQuery().getData().equals("/latestcharts")) {
            try {
                new SessionsDAO().getCurrentChat(update.getCallbackQuery()
                        .getMessage()
                        .getChatId())
                        .sendAllGraphs();
            } catch (TelegramApiException | SQLException | IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        if (update.getCallbackQuery().getData().equals("/settings_main"))
        {
            new SettingsKeyboard().returnToSettings(update);
        }
        if (update.getCallbackQuery().getData().equals("/notif_setting"))
        {
            new SettingsKeyboard().notificationSettings(update, null, null);
        }
        if (update.getCallbackQuery().getData().equals("/timezone_setting"))
        {
            new SettingsKeyboard().timezoneSettings(update);
        }

        if (update.getCallbackQuery().getData().equals("/satellite_setting"))
        {
            new SettingsKeyboard().satelliteSourceSettings(update, null, null);
        }

        if (update.getCallbackQuery().getData().equals("/noaa_dscovr"))
        {
            new SettingsKeyboard().satelliteSourceSettings(update, "NOAA DSCOVR", "/noaa_ace");
            new SessionsDAO().setSatelliteSource(1, chatId);
        }
        if (update.getCallbackQuery().getData().equals("/noaa_ace"))
        {
            new SettingsKeyboard().satelliteSourceSettings(update, "NOAA ACE", "/noaa_dscovr");
            new SessionsDAO().setSatelliteSource(2, chatId);
        }


        if (update.getCallbackQuery().getData().equals("/settings_notif_off"))
        {
            new SettingsKeyboard().notificationSettings(update, "Notifications Disabled", "/settings_notif_off");
            new SessionsDAO().getCurrentChat(chatId).setNotif(false);
        }
        if (update.getCallbackQuery().getData().equals("/settings_notif_on"))
        {
            new SettingsKeyboard().notificationSettings(update, "Notifications Enabled", "/settings_notif_off");
            new SessionsDAO().getCurrentChat(chatId).setNotif(true);
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