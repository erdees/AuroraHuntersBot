package ru.aurorahunters.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;

public class MessageHandler {
    private boolean isStarted = false;
    private int counter;
    private boolean isTimezoneConfigured;
    private String zone = "+00:00"; // default timezone if not configured
    private String history;
    private boolean isHistoryConfigured;
    private Long chatID;

    public MessageHandler(Long chatID) {
        this.chatID = chatID;
    }

    public void setBotStarted() {
        isStarted = true;
        counter = 0;
    }

    public String run(String input) throws SQLException, ParseException, IOException, TelegramApiException {
        if (counter > 0 && input.contains("/start") || counter > 0 && input.equals("/start@aurorahunters_bot")) {
            return "Bot is already started. Press /info to get list bot commands.";
        }
        else if (input.contains("/info") || input.equals("/info@aurorahunters_bot")) {
            return getInfo();
        }

        if (counter == 0) {
            counter++;
            return getInfo();
        }
        else if (input.contains("/chat") || input.equals("/chat@aurorahunters_bot")) {
            return "Please join our community: \nhttps://t.me/aurora_ru";
        }
        else if (input.equals("/map") || input.equals("/map@aurorahunters_bot")) {
            return getAuroraMap();
        }
        else if (input.contains("/last") || input.equals("/last@aurorahunters_bot")) {
            return GetDataFromDB.getLastValues(zone);
        }
        else if (input.contains("/time_settings") || input.equals("/time_settings@aurorahunters_bot")) {
            return setTimezone(input);
        }
        else if (input.contains("/weather") || input.equals("/weather@aurorahunters_bot")) {
            return getWeatherLinks();
        }
        else if (input.contains("/skycams") || input.equals("/skycams@aurorahunters_bot")) {
            return getCams();
        }
        else if (input.contains("/links") || input.equals("/links@aurorahunters_bot")) {
            return getLinks();
        }
        else if (input.equals("/graph_all") || input.equals("/graph_all@aurorahunters_bot")) {
            sendImage(TimeGraph.getBzGraph(zone));
            sendImage(TimeGraph.getSpeedGraph(zone));
            sendImage(TimeGraph.getDensityGraph(zone));
        }
        else if (input.equals("/graph_bz") || input.equals("/graph_bz@aurorahunters_bot")) {
            sendImage(TimeGraph.getBzGraph(zone));
        }
        else if (input.equals("/graph_speed") || input.equals("/graph_speed@aurorahunters_bot")) {
            sendImage(TimeGraph.getSpeedGraph(zone));
        }
        else if (input.equals("/graph_density") || input.equals("/graph_density@aurorahunters_bot")) {
            sendImage(TimeGraph.getDensityGraph(zone));
        }
        else if (isHistoryConfigured && input.matches("\\/\\w+") && !input.equals("/history")) {
            if (input.equals("/history_text") || input.equals("/history_text@aurorahunters_bot")) {
                return getHistoryData();
            }
            else if (input.equals("/history_graph_bz") || input.equals("/history_graph_bz@aurorahunters_bot")) {
                sendImage(ArchiveTimeGraph.getBzGraph(history));
            }
            else if (input.equals("/history_graph_speed") || input.equals("/history_graph_speed@aurorahunters_bot")) {
                sendImage(ArchiveTimeGraph.getSpeedGraph(history));
            }
            else if (input.equals("/history_graph_density") || input.equals("/history_graph_density@aurorahunters_bot")) {
                sendImage(ArchiveTimeGraph.getDensityGraph(history));
            }
            else if (input.equals("/history_graph_all") || input.equals("/history_graph_all@aurorahunters_bot")) {
                sendImage(ArchiveTimeGraph.getBzGraph(history));
                sendImage(ArchiveTimeGraph.getSpeedGraph(history));
                sendImage(ArchiveTimeGraph.getDensityGraph(history));
            }
        }
        else if (input.contains("/history") || input.contains("/contains@aurorahunters_bot")) {
            return setHistoryDate(input);
        }
        return "";
    }

    private String getInfo() {
        return "Hi, i'm AuroraHunters bot. <b>See my available commands below:</b>\n" +
                "/start to start the bot;\n" +
                "/stop to stop the bot;\n" +
                "/info to see this message;\n" +
                "/last to see last values from DSCOVR satellite;\n" +
                "/history to get old DSCOVR values;\n" +
                "/time_settings to change your timezone;\n" +
                "/links to get useful links\n";
    }

    private String setTimezone(String input) {
        if (input.contains("/time_settings") || input.equals("/time_settings@aurorahunters_bot")) {
            String regex = "^(?:Z|[+-](?:2[0-3]|[01][0-9]):([03][00]))$";
            String[] temp;
            String delimiter = " ";
            temp = input.split(delimiter);
            String command = temp[0];
            try {
                String argument = temp[1];
                if (argument.matches(regex)) {
                    isTimezoneConfigured = true;
                    zone = argument;
                    return "Your timezone now is <b>UTC" + argument + "</b>";
                } else {
                    return "Please type correct timezone, e.g. /time_settings +03:00";
                }
            } catch (Exception e) {
                if (!isTimezoneConfigured) {
                    return "<b>Timezone is not configured.</b> Please enter required timezone in UTC format: \n" +
                            "e.g. command for Moscow Standard Time will be /time_settings +03:00";
                } else if (isTimezoneConfigured) {
                    return "Configured timezone is <b>UTC" + zone + ".</b> If you need to change it, please enter required timezone in UTC format: \n" +
                            "e.g. command for Moscow Standard Time will be /time_settings +03:00";
                }
            }
        }
        return "";
    }

    private String setHistoryDate(String input) {
        if (input.contains("/history") || input.equals("/history@aurorahunters_bot")) {
            String regex = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
            String[] temp;
            String delimiter = " ";
            temp = input.split(delimiter);
            String command = temp[0];
            try {
                String argument = temp[1];
                if (argument.matches(regex)) {
                    isHistoryConfigured = true;
                    history = argument;
                    return "All archive data will be shown for <b>" + history + ".</b>\n" +
                            "Highest 24 values will be shown for each hour.\n" +
                            "Please use following commands: \n" +
                            "/history_text - to get text table; \n" +
                            "/history_graph_bz - to get bz_gsm graph for " + history + "\n" +
                            "/history_graph_speed - to get speed graph for " + history + "\n" +
                            "/history_graph_density - to get density graph for " + history + "\n" +
                            "/history_graph_all - to get all three graphs for " + history + "\n";
                } else {
                    return "Please type correct date, e.g. /history yyyy-MM-dd format";
                }
            } catch (Exception e) {
                if (!isHistoryConfigured) {
                    return "Archive is not configured. Please enter required date in <b>yyyy-MM-dd</b> format: \n" +
                            "e.g. command for <b>July 01 of 2020</b> will be \n" +
                            "/history 2020-07-01";
                } else if (isHistoryConfigured) {
                    return "Archive is configured for<b> " + history + ".</b>\n If you need to change it, please type it in required format: \n" +
                            "e.g. command for <b>July 01 of 2020</b> will be \n/history 2020-07-01\n" +
                            "All archive data will be shown for <b>" + history + ".</b>\n" +
                            "Highest 24 values will be shown for each hour.\n" +
                            "Please use following commands: \n" +
                            "/history_text - to get text table; \n" +
                            "/history_graph_bz - to get bz_gsm graph for " + history + "\n" +
                            "/history_graph_speed - to get speed graph for " + history + "\n" +
                            "/history_graph_density - to get density graph for " + history + "\n" +
                            "/history_graph_all - to get all three graphs for " + history + "\n";
                }
            }
        }
        return "";
    }

    public String getHistoryData() throws SQLException, ParseException {
        if (isHistoryConfigured) {
            return GetDataFromDB.getHistoryValues(history);
        }
        else return "Archive is not configured. Please enter required date in <b>yyyy-MM-dd</b> format: \n" +
                "e.g. command for <b>July 01 of 2020</b> will be \n" +
                "/history 2020-07-01";
    }

    private String getLinks() {
        return "Here is a lisot of some useful links which can help you:\n" +
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

    public String getTimeZone() {
        return zone;
    }

    public void sendImage(File image) throws FileNotFoundException, TelegramApiException {
        AuroraBot sendGraph = new AuroraBot();
        SendPhoto graph = new SendPhoto()
                .setPhoto("AuroraHuntersBot_Graph", new FileInputStream(image))
                .setChatId(chatID);
        sendGraph.execute(graph);
    }
}
