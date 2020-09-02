package ru.aurorahunters.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aurorahunters.bot.controller.GetDataFromDB;
import ru.aurorahunters.bot.graphbuilder.ArchiveTimeGraph;
import ru.aurorahunters.bot.graphbuilder.NewTimeGraph;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

public class MessageHandler {
    private Long chatID;
    private boolean isStarted = false;
    private boolean isTimezoneConfigured;
    private String timezone = "+00:00"; // default timezone if not configured
    private boolean isHistoryConfigured;
    private String archiveDate;
    private boolean isNotifConfigured;

    public MessageHandler(Long chatID) {
        this.chatID = chatID;
    }

    public MessageHandler(Long chatID, boolean isStarted, boolean isTimezoneConfigured, String timezone, boolean
            isHistoryConfigured, String archiveDate, boolean isNotifConfigured) {
        this.chatID = chatID;
        this.isStarted = isStarted;
        this.isTimezoneConfigured = isTimezoneConfigured;
        this.timezone = timezone;
        this.isHistoryConfigured = isHistoryConfigured;
        this.archiveDate = archiveDate;
        this.isNotifConfigured = isNotifConfigured;
    }

    public String setBotStarted() {
        isStarted = true;
        return getInfo();
    }

    /**
     * Main method which threats reaction on user's messages.
     * @param input a String parameter which should be parsed and processed if necessary.
     * @return a String which is basically should be a message which will be sent to a user.
     */
    public String respondMessage(String input) throws SQLException, ParseException, IOException, TelegramApiException {
        if (isStarted) {
            if (input.equals("/info") || input.equals("/info" + Config.getBotUsername())) {
                return getInfo();
            }
            if (input.equals("/start") || input.equals("/start" + Config.getBotUsername())) {
                return "Bot is already started. Type /info to see available commands.";
            }
            if (input.equals("/chat") || input.equals("/chat" + Config.getBotUsername())) {
                return "Please join our community: \nhttps://t.me/aurora_ru";
            }
            if (input.equals("/map") || input.equals("/map" + Config.getBotUsername())) {
                return getAuroraMap();
            }
            if (input.equals("/last") || input.equals("/last" + Config.getBotUsername())) {
                return GetDataFromDB.getLastValues(timezone);
            }
            if (input.contains("/time_settings") || input.equals("/time_settings" + Config.getBotUsername())) {
                return setTimezone(input);
            }
            if (input.equals("/notif_on") || input.equals("/notif_on" + Config.getBotUsername())) {
                return setNotif(true);
            }
            if (input.equals("/notif_off") || input.equals("/notif_off" + Config.getBotUsername())) {
                return setNotif(false);
            }
            if (input.equals("/weather") || input.equals("/weather" + Config.getBotUsername())) {
                return getWeatherLinks();
            }
            if (input.equals("/skycams") || input.equals("/skycams" + Config.getBotUsername())) {
                return getCams();
            }
            if (input.equals("/links") || input.equals("/links" + Config.getBotUsername())) {
                return getLinks();
            }
            if (input.equals("/graph_all") || input.equals("/graph_all" + Config.getBotUsername())) {
                sendImage(NewTimeGraph.getBzGraph(timezone));
                sendImage(NewTimeGraph.getSpeedGraph(timezone));
                sendImage(NewTimeGraph.getDensityGraph(timezone));
            }
            if (input.equals("/graph_bz") || input.equals("/graph_bz" + Config.getBotUsername())) {
                sendImage(NewTimeGraph.getBzGraph(timezone));
            }
            if (input.equals("/graph_speed") || input.equals("/graph_speed" + Config.getBotUsername())) {
                sendImage(NewTimeGraph.getSpeedGraph(timezone));
            }
            if (input.equals("/graph_density") || input.equals("/graph_density" + Config.getBotUsername())) {
                sendImage(NewTimeGraph.getDensityGraph(timezone));
            }
            if (isHistoryConfigured && input.matches("\\/\\w+") && !input.equals("/history") ||
                    !input.equals("/history" + Config.getBotUsername())) {
                if (input.equals("/history_text") || input.equals("/history_text" + Config.getBotUsername())) {
                    return getHistoryData();
                }
                if (input.equals("/history_graph_bz") || input.equals("/history_graph_bz" +
                        Config.getBotUsername())) {
                    sendImage(ArchiveTimeGraph.getBzGraph(archiveDate));
                }
                if (input.equals("/history_graph_speed") || input.equals("/history_graph_speed" +
                        Config.getBotUsername())) {
                    sendImage(ArchiveTimeGraph.getSpeedGraph(archiveDate));
                }
                if (input.equals("/history_graph_density") || input.equals("/history_graph_density" +
                        Config.getBotUsername())) {
                    sendImage(ArchiveTimeGraph.getDensityGraph(archiveDate));
                }
                if (input.equals("/history_graph_all") || input.equals("/history_graph_all" +
                        Config.getBotUsername())) {
                    sendImage(ArchiveTimeGraph.getBzGraph(archiveDate));
                    sendImage(ArchiveTimeGraph.getSpeedGraph(archiveDate));
                    sendImage(ArchiveTimeGraph.getDensityGraph(archiveDate));
                }
            }
            else if (input.contains("/history") || input.contains("/history" + Config.getBotUsername())) {
                return setHistoryDate(input);
            }
            return "";
        }
        else {
            return "Bot is is not started. Press /start to initialize it.";
        }
    }

    private String getInfo() {
        return "Hi, i'm AuroraHunters bot. <b>See my available commands below:</b>\n" +
                "/start to start the bot;\n" +
                "/stop to stop the bot;\n" +
                "/info to see this message;\n" +
                "/last to see last values from DSCOVR satellite;\n" +
                "/history to get old DSCOVR values;\n" +
                "/time_settings to change your timezone;\n" +
                "/notif_on to enable notifications;\n" +
                "/notif_off to disable notifications;\n" +
                "/links to get useful links\n";
    }

    /**
     * 1st way to configure timezone by using /time_settings command. Checks a timezone format, and if it is correct,
     * adjust user timezone configuration. If not, a String with instruction will be returned.
     * @param input with timezone in yyy-MM-dd format.
     * @return command execution result.
     */
    private String setTimezone(String input) {
        if (input.contains("/time_settings") || input.equals("/time_settings" + Config.getBotUsername())) {
            String regex = "^(?:Z|[+-](?:2[0-3]|[01][0-9]):([03][00]))$";
            String[] temp;
            temp = input.split(" ");
            try {
                String argument = temp[1];
                if (argument.matches(regex)) {
                    isTimezoneConfigured = true;
                    timezone = argument;
                    setDbTimezone();
                    return "Your timezone now is <b>UTC" + argument + "</b>";
                } else {
                    return "Please type correct timezone, e.g. /time_settings +03:00";
                }
            } catch (Exception e) {
                if (!isTimezoneConfigured) {
                    return "<b>Timezone is not configured.</b> To configure it, just <b>share with me your GPS " +
                            "location.</b>\n" +
                            "If GPS is not suitable way, you can configure it manually by entering required timezone " +
                            "in UTC format: \n" +
                            "e.g. command for Moscow Standard Time will be /time_settings +03:00";
                } else {
                    return "Configured timezone is <b>UTC" + timezone + ".</b> If you need to change it, " +
                            "just <b>share with me your GPS location</b> or enter it manually in UTC format: \n" +
                            "e.g. command for Moscow Standard Time will be /time_settings +03:00";
                }
            }
        }
        return "";
    }

    /**
     * 2nd way to configure a timezone in the bot. Configures a timezone according to gps mark which user can send
     * using his phone. This method comes from SessionHandler where it calls using TimeClass.getGpsTimezone.
     * @param input timezone, e.g. UTC+00:00.
     * @return string with execution result.
     */
    public String setGpsTimezone(String input) throws SQLException {
        isTimezoneConfigured = true;
        timezone = input;
        setDbTimezone();
        return "Your timezone now is <b>UTC" + timezone + "</b>";
    }

    /** Method which adjust a timezone in a Database. */
    private void setDbTimezone() throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        final String SQL = "UPDATE sessions SET is_timezone=?, timezone=? where chat_id=?;";
        PreparedStatement ps = Config.getDbConnection().prepareStatement(SQL);
        try {
            ps.setBoolean(1, true);
            ps.setString(2,timezone);
            ps.setLong(3, chatID);
            ps.executeUpdate();
            Config.getDbConnection().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method which checks entered by user date and translates it to setDbHistoryDate() if check was successful.
     * If it not, a String with instructions will be returned.
     * @param input date in yyy-MM-dd format.
     * @return a String with result.
     */
    private String setHistoryDate(String input) {
        if (input.contains("/history") || input.equals("/history" + Config.getBotUsername())) {
            String regex = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
            String[] temp;
            temp = input.split(" ");
            try {
                String argument = temp[1];
                if (argument.matches(regex)) {
                    isHistoryConfigured = true;
                    archiveDate = argument;
                    setDbHistoryDate();
                    return "All archive data will be shown for <b>" + archiveDate + ".</b>\n" +
                            "Highest 24 values will be shown for each hour.\n" +
                            "Please use following commands: \n" +
                            "/history_text - to get text table; \n" +
                            "/history_graph_bz - to get bz_gsm graph for " + archiveDate + "\n" +
                            "/history_graph_speed - to get speed graph for " + archiveDate + "\n" +
                            "/history_graph_density - to get density graph for " + archiveDate + "\n" +
                            "/history_graph_all - to get all three graphs for " + archiveDate + "\n";
                } else {
                    return "Please type correct date, e.g. /history yyyy-MM-dd format";
                }
            } catch (Exception e) {
                if (!isHistoryConfigured) {
                    return "Archive is not configured. Please enter required date in <b>yyyy-MM-dd</b> format: \n" +
                            "e.g. command for <b>July 01 of 2020</b> will be \n" +
                            "/history 2020-07-01";
                } else {
                    return "Archive is configured for<b> " + archiveDate + ".</b>\n If you need to change it, please " +
                            "type it in required format: \n" +
                            "e.g. command for <b>July 01 of 2020</b> will be \n/history 2020-07-01\n" +
                            "All archive data will be shown for <b>" + archiveDate + ".</b>\n" +
                            "Highest 24 values will be shown for each hour.\n" +
                            "Please use following commands: \n" +
                            "/history_text - to get text table; \n" +
                            "/history_graph_bz - to get bz_gsm graph for " + archiveDate + "\n" +
                            "/history_graph_speed - to get speed graph for " + archiveDate + "\n" +
                            "/history_graph_density - to get density graph for " + archiveDate + "\n" +
                            "/history_graph_all - to get all three graphs for " + archiveDate + "\n";
                }
            }
        }
        return "";
    }

    /** Configure archive date in a Database as a user parameter which can be used later. */
    private void setDbHistoryDate() throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        final String SQL = "UPDATE sessions SET is_archive=?, archive=? where chat_id=?;";
        PreparedStatement ps = Config.getDbConnection().prepareStatement(SQL);
        try {
            ps.setBoolean(1, true);
            ps.setString(2,archiveDate);
            ps.setLong(3, chatID);
            ps.executeUpdate();
            Config.getDbConnection().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a String from GetDataFromDB.getHistoryValues.
     * @return String with a text message.
     */
    private String getHistoryData() throws SQLException, ParseException {
        if (isHistoryConfigured) {
            return GetDataFromDB.getHistoryValues(archiveDate);
        }
        else return "Archive is not configured. Please enter required date in <b>yyyy-MM-dd</b> format: \n" +
                "e.g. command for <b>July 01 of 2020</b> will be \n" +
                "/history 2020-07-01";
    }

    /**
     * Method which turns on/off notifications on high solar wind parameters.
     * @param set boolean where true is on and false is off.
     * @return an operation result String message
     */
    private String setNotif (boolean set) throws SQLException {
        if (set) {
            setDbNotif(true);
            return "Notifications enabled.";
        }
        else  {
            setDbNotif(false);
            return "Notifications disabled.";
        }
    }

    /**
     * Method which turns on/off notifications on high solar wind parameters by UPDATE query in a Database.
     * @param param boolean where true is on and false is off.
     */
    private void setDbNotif(boolean param) throws SQLException {
        Config.getDbConnection().setAutoCommit(false);
        final String SQL = "UPDATE sessions SET is_notif=? where chat_id=?;";
        PreparedStatement ps = Config.getDbConnection().prepareStatement(SQL);
        try {
            ps.setBoolean(1, param);
            ps.setLong(2, chatID);
            ps.executeUpdate();
            Config.getDbConnection().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private String getLinks() {
        return "Here is a list of some useful links which can help you:\n" +
                "/weather to get relevant weather links\n" +
                "/skycams to get sky webcam links\n" +
                "/map - to get aurora lights map\n" +
                "/chat - official project chat";
    }

    private String getWeatherLinks() {
        return "Weather links:\n" +
                "https://weather.us/model-charts/deu-hd/leningrad/total-cloud-coverage/2019-0z.html\n" +
                "https://kachelmannwetter.com/de/sat/leningrad/satellit-staub-15min/2019-0z.html\n" +
                "https://www.windy.com";
    }

    private String getCams() {
        return "Tampere (61.6316413,23.5501255)\n" +
                "https://www.ursa.fi/yhd/tampereenursa/Pics/latest-3.jpg\n" +
                "Hankasaalmi(62.38944,26.427378)\n" +
                "http://murtoinen.jklsirius.fi/ccd/skywatch/\n" +
                "Abisko (68.3494106 – 18.8212895)\n" +
                "https://auroraskystation.se/live/\n" +
                "Svalbard (78.6224431,15.572671)\n" +
                "https://aurorainfo.eu/aurora-live-cameras/svalbard-norway-all-sky-aurora-live-camera.jpg";
    }

    private String getAuroraMap() {
        return "Aurora map in north Europe: \n" +
                "http://auroralights.ru/%d0%ba%d0%b0%d1%80%d1%82%d0%b0-%d0%b3%d0%b4%d0%b5-%d1%81%d0%bc%d0%be%d1%82%d1%" +
                "80%d0%b5%d1%82%d1%8c-%d1%81%d0%b5%d0%b2%d0%b5%d1%80%d0%bd%d1%8b%d0%b5-%d1%81%d0%b8%d1%8f%d0%bd%d0%b8%d1%8f/";
    }

    /**
     * Method which sends generated graph to user.
     * @param image is a generated and retrieved as a .png file TimeGraph image chart.
     */
    private void sendImage(File image) throws FileNotFoundException, TelegramApiException {
        AuroraBot sendGraph = new AuroraBot();
        SendPhoto graph = new SendPhoto()
                .setPhoto("AuroraHuntersBot_Graph", new FileInputStream(image))
                .setChatId(chatID);
        sendGraph.execute(graph);
    }
}
