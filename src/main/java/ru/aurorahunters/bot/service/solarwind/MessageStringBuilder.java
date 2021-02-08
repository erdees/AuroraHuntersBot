package ru.aurorahunters.bot.service.solarwind;

import ru.aurorahunters.bot.dao.DataDAO;
import ru.aurorahunters.bot.model.HistoricalDate;
import ru.aurorahunters.bot.model.SolarWindData;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class MessageStringBuilder {

    private static final int HOUR = 59; //from 00 to 59 minute

    /**
     * Forms a message with latest 10 solar wind values for Telegram message as a String.
     * @param timezone which goes from MessageHandler.
     * @return a String with formatted for the Telegram.
     */
    public String getLastValues(String timezone) throws SQLException, ParseException {
        StringBuilder sb = new StringBuilder();
        String timeZoneToMessage = timezone.split(":")[0];
        String firstLine = String.format("%s%s%n", "<pre>", "Waiting time: " +
                new ValueCalculator().getWaitingTime() + "\n");
        String secondLine = String.format("%4s\t%s\t%3s\t%s\t%4s\t%s\t%3s%n",
                "BZ ", "|", "S ", "|", "PD", "|", "UTC" +timeZoneToMessage);
        sb.append(firstLine).append(secondLine);
        buildStringBody(timezone, sb);
        return sb.toString();
    }

    /**
     * Retreive latest 10 density, speed and bz values from DAO and format them for Telegram
     * message as a String.
     * @param timezone which goes from MessageHandler.
     * @param sb for StringBuilder increment
     */
    private void buildStringBody(String timezone, StringBuilder sb) throws SQLException, ParseException {
        String lastLine = "</pre>\nlatest bz graph /graph_bz" +
                "\nlatest speed graph /graph_speed" +
                "\nlatest density graph /graph_density" +
                "\nall latest graphs /graph_all";
        DecimalFormat chatOutput = new DecimalFormat("###.#");
        Iterator<Map.Entry<Date, SolarWindData>> iterator =
                new DataDAO().getLatestValues(timezone).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Date, SolarWindData> pair = iterator.next();
            String dateString = new SimpleDateFormat("HH:mm").format(pair.getKey());
            sb.append(String.format("%4s\t%s\t%3s\t%s\t%4s\t%s\t%3s%n",
                    chatOutput.format(pair.getValue().getBzGsm()), "|",
                    Math.round(pair.getValue().getSpeed()), "|",
                    chatOutput.format(pair.getValue().getDensity()), "|", dateString));
            if (!iterator.hasNext()) {
                sb.append(lastLine);
            }
        }
    }

    /**
     * Method retrieves history values of solar wind speed, density and bz according to arg date and forms a Telegram
     * format message which can be called by the user from MessageHandler
     * @param date in yyy-MM-dd format which will be a part of SQL statement for history query
     * @return a Telegram formatted message which will be called from MessageHandler
     */
    public String getHistoryValues(String date) throws SQLException, ParseException {
        LinkedHashMap<Integer, ArrayList<Double>> map = new LinkedHashMap<>();

        if (date.matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")) {
            HistoricalDate historicalDate = determineDayRange(date);
            assignHistoryValues(map, historicalDate);
            if (map.isEmpty()) {
                return "Please choose another date. There is no data for " + date;
            } else {
                return buildAnswerString(date, map);
            }
        } else {
            return "Wrong format. Please type correct command.";
        }
    }

    /**
     * Process raw history data from DAO, calculate highest values for each hour and put them to
     * a HashMap.
     * @param map where processed values will be stored.
     * @param historicalDate required to request raw data from DAO
     */
    private void assignHistoryValues(LinkedHashMap<Integer, ArrayList<Double>> map, HistoricalDate historicalDate) throws SQLException, ParseException {
        ArrayList<Double> densityList = new ArrayList<>();
        ArrayList<Double> speedList = new ArrayList<>();
        ArrayList<Double> bzList = new ArrayList<>();
        for (Map.Entry<Date, SolarWindData> pair :
                new DataDAO().getHistoryValues(historicalDate).entrySet()) {
            Date sqlDate = pair.getKey();
            double density = pair.getValue().getDensity();
            double speed = pair.getValue().getSpeed();
            double bzGsm = pair.getValue().getBzGsm();
            String hourVar = new SimpleDateFormat("HH").format(sqlDate);
            String minVar = new SimpleDateFormat("mm").format(sqlDate);
            densityList.add(density);
            bzList.add(bzGsm);
            speedList.add(speed);
            if (Integer.parseInt(minVar) == HOUR) {
                Double maxDensity = Collections.max(densityList);
                Double maxSpeed = Collections.max(speedList);
                Double maxBz = Collections.min(bzList);
                map.put(Integer.parseInt(hourVar), new ArrayList<>(Arrays.asList(maxDensity, maxSpeed, maxBz)));
                densityList = new ArrayList<>();
                speedList = new ArrayList<>();
                bzList = new ArrayList<>();
            }
        }
    }

    /**
     * Method which determine a range for database request.
     * @param date in a String format which will calculate prior and end dates.
     * @return HistoricalDate object containing two Strings with required data.
     */
    private HistoricalDate determineDayRange(String date) throws ParseException {
        SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd");
        Date start = formattedDate.parse(date);
        Date end;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, 1);
        end = calendar.getTime();
        String initialDay = formattedDate.format(start);
        String finalDay = formattedDate.format(end);

        return new HistoricalDate(initialDay, finalDay);
    }

    /**
     * Method do answer build job: process prepared data to a formatted Telegram message string.
     * @param date Date to show in a message.
     * @param map prepared data.
     * @return string with prepared message.
     */
    private String buildAnswerString(String date, LinkedHashMap<Integer, ArrayList<Double>> map) {
        StringBuilder sb = new StringBuilder();
        DecimalFormat chatOutput = new DecimalFormat("###.#");
        String firstLine = String.format("%s%s%n", "<pre>","History values for " + date + "\n");
        String secondLine = String.format("%4s\t%s\t%3s\t%s\t%4s\t%s\t%3s%n", "BZ ", "|", "S", "|", "PT", "|", "Time" );
        sb.append(firstLine).append(secondLine);
        for(Map.Entry<Integer, ArrayList<Double>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<Double> value = entry.getValue();
            sb.append(String.format("%4s\t%s\t%3s\t%s\t%4s\t%s\t%3s%n", chatOutput.format(value.get(2)) , "|", Math.round(value.get(1)), "|", chatOutput.format(value.get(0)), "|", key));
        }
        sb.append("</pre>"); //last line
        return sb.toString();
    }
}
