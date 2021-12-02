package ru.aurorahunters.bot.notification;

import ru.aurorahunters.bot.model.solardata.SolarWindData;

import java.text.DecimalFormat;
import java.util.List;

public class MessageBuilder {

    private static final String FIRST_LINE =
            String.format("%s%s%n", "<pre>","Notification: high solar wind parameters:\n");
    private static final String SECOND_LINE =
            String.format("%4s\t%s\t%3s\t%s\t%4s%n", "BZ ", "|", "S ", "|", "PD");
    private static final String LAST_LINE = "</pre>";

    /**
     * Generates a String message in Telegram format with notification.
     * @return a String with result.
     */
    public String getAlarmString(List<SolarWindData> lastFiveMin) {
        SolarWindData lastMinValues = lastFiveMin.get(lastFiveMin.size() -1);
        Double lastMinDens = lastMinValues.getDensity();
        Double lastMinSpeed = lastMinValues.getSpeed();
        Double lastMinBz = lastMinValues.getBzGsm();
        DecimalFormat outputFormat = new DecimalFormat("###.#");
        StringBuilder sb = new StringBuilder();

        sb.append(FIRST_LINE).append(SECOND_LINE);
        sb.append(String.format("%4s\t%s\t%3s\t%s\t%4s%n", outputFormat.format(lastMinBz), "|",
                Math.round(lastMinSpeed), "|", outputFormat.format(lastMinDens)));
        sb.append(LAST_LINE);
        return sb.toString();
    }
}
