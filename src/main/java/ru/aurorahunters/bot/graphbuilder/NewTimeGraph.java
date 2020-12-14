package ru.aurorahunters.bot.graphbuilder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.utils.TimeClass;
import ru.aurorahunters.bot.controller.GetSunWindDataFromDB;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class NewTimeGraph {

    /** Throughput method to generate a proton density graph  */
    public static File getDensityGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(getGraph(timezone, GraphTypeEnum.DENSITY,
                getCurrentValues(timezone, GraphTypeEnum.DENSITY)));
    }

    /** Throughput method to generate a solar speed graph  */
    public static File getSpeedGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(getGraph(timezone, GraphTypeEnum.SPEED,
                getCurrentValues(timezone, GraphTypeEnum.SPEED)));
    }

    /** Throughput method to generate a bz graph  */
    public static File getBzGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(getGraph(timezone, GraphTypeEnum.BZ_GSM,
                getCurrentValues(timezone, GraphTypeEnum.BZ_GSM)));
    }

    /** Throughput method to generate an archive proton density graph  */
    public static File getDensityArchiveGraph(String date) throws ParseException, SQLException, IOException {
        return getGraphFile(getGraph(date, GraphTypeEnum.DENSITY_H,
                getArchiveValues(date, GraphTypeEnum.DENSITY_H)));
    }

    /** Throughput method to generate an archive solar speed graph  */
    public static File getSpeedArchiveGraph(String date) throws ParseException, SQLException, IOException {
        return getGraphFile(getGraph(date, GraphTypeEnum.SPEED_H,
                getArchiveValues(date, GraphTypeEnum.SPEED_H)));
    }

    /** Throughput method to generate an archive bz graph  */
    public static File getBzArchiveGraph(String date) throws ParseException, SQLException, IOException {
        return getGraphFile(getGraph(date, GraphTypeEnum.BZ_GSM_H,
                getArchiveValues(date, GraphTypeEnum.BZ_GSM_H)));
    }

    /**
     * Method generates a graph according to received as an argument TreeMap.
     * @param timezoneOrDate necessary for query field. Parameter already verified in other class.
     * @param e GraphTypeEnum which necessary to render a text values for each chart.
     * @param m is Map with necessary for chart generation values.
     * @return JFreeChart object e.g. graph which can be placed to a file.
     */
    private static JFreeChart getGraph(String timezoneOrDate, GraphTypeEnum e,
                                       TreeMap<Date, Double> m) throws SQLException, ParseException {
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | " + Config.getWEBSITE()
                + " Telegram Bot (" + Config.getBotUsername() + ") | " + TimeClass.GetCurrentGmtTime());
        for(Map.Entry<Date, Double> entry : m.entrySet()) {
            Date time_tag = entry.getKey();
            Double value = entry.getValue();
            timeChart.add(new Minute(time_tag), value);
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);
        JFreeChart chart = getChartByType(timezoneOrDate, e, dataset);
        XYPlot plot = chart.getXYPlot();
        setChartStyle(plot, chart);
        setRangeAndColor(plot, e);
        return chart;
    }

    /**
     * This method creates the chart with necessary labels according to chart type (last or history).
     * @param timezoneOrDate which is timezone or archive date which depend on throughput methods.
     * @param e GraphTypeEnum which required to detect chart type.
     * @param d created earlier TimeSeriesCollection.
     * @return created JFreeChart object.
     */
    private static JFreeChart getChartByType(String timezoneOrDate, GraphTypeEnum e, TimeSeriesCollection d)
            throws SQLException {
        JFreeChart chart;
        if (e == GraphTypeEnum.BZ_GSM || e == GraphTypeEnum.SPEED || e == GraphTypeEnum.DENSITY) {
            chart = ChartFactory.createTimeSeriesChart(
                    e.printName + " - last 3 hours",
                    "Time (UTC" + timezoneOrDate + ") | " + "Waiting time: " +
                            GetSunWindDataFromDB.getWaitingTime(),
                    e.printName, d, true, true, false);
        } else {
            chart = ChartFactory.createTimeSeriesChart(
                    e.printName + " | Archive for " + timezoneOrDate,
                    "Time (UTC+00:00)" , e.printName, d, true, true, false);
        }
        return chart;
    }

    /**
     * Configure Interval Markers, colors and other unique parameters for generating graph.
     * @param plot of XYPlot object.
     * @param e GraphTypeEnum with required for each graph fields.
     */
    private static void setRangeAndColor(XYPlot plot, GraphTypeEnum e) {
        if (e == GraphTypeEnum.DENSITY || e == GraphTypeEnum.DENSITY_H) {
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeDensQuietStart(),
                    Config.getGraphRangeDensQuietEnd(), getColor(Config.getGraphColorQuiet())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeDensModerateStart(),
                    Config.getGraphRangeDensModerateEnd(), getColor(Config.getGraphColorModerate())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeDensIncreasedStart(),
                    Config.getGraphRangeDensIncreasedEnd(), getColor(Config.getGraphColorIncreased())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeDensHighStart(),
                    Config.getGraphRangeDensHighEnd(), getColor(Config.getGraphColorHigh())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeDensExtremeStart(),
                    Config.getGraphRangeDensExtremeEnd(), getColor(Config.getGraphColorExtreme())));
        }
        if (e == GraphTypeEnum.SPEED || e == GraphTypeEnum.SPEED_H) {
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeSpeedQuietStart(),
                    Config.getGraphRangeSpeedQuietEnd(), getColor(Config.getGraphColorQuiet())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeSpeedModerateStart(),
                    Config.getGraphRangeSpeedModerateEnd(), getColor(Config.getGraphColorModerate())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeSpeedIncreasedStart(),
                    Config.getGraphRangeSpeedIncreasedEnd(), getColor(Config.getGraphColorIncreased())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeSpeedHighStart(),
                    Config.getGraphRangeSpeedHighEnd(), getColor(Config.getGraphColorHigh())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeSpeedExtremeStart(),
                    Config.getGraphRangeSpeedExtremeEnd(), getColor(Config.getGraphColorExtreme())));
        }
        if (e == GraphTypeEnum.BZ_GSM || e == GraphTypeEnum.BZ_GSM_H) {
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBzQuietStart(),
                    Config.getGraphRangeBzQuietEnd(), getColor(Config.getGraphColorQuiet())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBzModerateStart(),
                    Config.getGraphRangeBzModerateEnd(), getColor(Config.getGraphColorModerate())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBzIncreasedStart(),
                    Config.getGraphRangeBzIncreasedEnd(), getColor(Config.getGraphColorIncreased())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBzHighStart(),
                    Config.getGraphRangeBzHighEnd(), getColor(Config.getGraphColorHigh())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBzExtremeStart(),
                    Config.getGraphRangeBzExtremeEnd(), getColor(Config.getGraphColorExtreme())));
        }
    }

    /** Configure styles, fonts and colors which is the same for all solar wind charts. */
    private static void setChartStyle(XYPlot plot, JFreeChart chart) {
        chart.setBackgroundPaint(Color.BLACK);
        chart.getTitle().setPaint(Color.white);
        chart.getLegend().setBackgroundPaint(Color.BLACK);
        chart.getLegend().setItemPaint(Color.white);
        plot.setBackgroundPaint(Color.BLACK);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.white);
        plot.getDomainAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setTickLabelPaint(Color.white);
        plot.getDomainAxis().setLabelPaint(Color.white);
        plot.getRangeAxis().setLabelPaint(Color.white);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);
    }

    /**
     * Parses rgba from String by placing and splitting it to an array.
     * @param color which goes from the config.properties.
     * @return Color in rgba.
     */
    private static Color getColor(String color) {
        String[] rgba;
        rgba = color.split(",");
        int r = Integer.parseInt(rgba[0].trim());
        int g = Integer.parseInt(rgba[1].trim());
        int b = Integer.parseInt(rgba[2].trim());
        int a = Integer.parseInt(rgba[3].trim());
        return new Color(r,g,b,a);
    }

    /**
     * Returns a file with generated graph of JFreeChart object.
     * @param c chart from prepared JFreeChart object.
     * @return a file.
     */
    private static File getGraphFile(JFreeChart c) throws IOException {
        File file = new File(".png");
        ChartUtils.saveChartAsPNG(file, c, 600, 400);
        return file;
    }

    /**
     * Method which retrieves from a Database values for chart generation in "online" mode e.g. 180 last values.
     * @param timezone parameter which determines which for which timezone chart should be generated.
     * @param e GraphTypeEnum with required for each graph fields.
     * @return TreeMap with Date and Double values which is datasource for final chart generation.
     */
    private static TreeMap<Date, Double> getCurrentValues(String timezone, GraphTypeEnum e)
            throws SQLException, ParseException {
        TreeMap<Date, Double> out = new TreeMap<>();
        String SQL_SELECT = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone + "' at time zone 'utc', "
                + e.dbKey + " from data ORDER BY time_tag desc limit 180) SELECT * FROM t ORDER BY timezone ASC";
        PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(SQL_SELECT);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double value = resultSet.getDouble(2);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            out.put(date, value);
        }
        return out;
    }

    /**
     * Method which retrieves from a Database values for chart generation in "archive" mode according to user request.
     * @param date parameter which determines which for which year, month and day chart should be generated.
     * @param e GraphTypeEnum with required for each graph fields.
     * @return TreeMap with Date and Double values which is datasource for final chart generation.
     */
    private static TreeMap<Date, Double> getArchiveValues(String date, GraphTypeEnum e)
            throws SQLException, ParseException {
        TreeMap<Date, Double> out = new TreeMap<>();
        HashMap<Date, Double> valueList = new HashMap<>();
        SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd");
        if (date.matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")) {
            Date indicatedDay = formattedDate.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(indicatedDay);
            c.add(Calendar.DATE, 1);
            Date nextDay = c.getTime();
            String initialDay = formattedDate.format(indicatedDay);
            String finalDay = formattedDate.format(nextDay);
            String SQL_TEST = "SELECT time_tag, " + e.dbKey + " FROM data WHERE time_tag >= '" + initialDay +
                    "' AND time_tag < '" + finalDay + "';";
            PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(SQL_TEST);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Timestamp time_tag = resultSet.getTimestamp(1);
                double value = resultSet.getDouble(2);
                Date sqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
                String minVar = new SimpleDateFormat("mm").format(sqlDate);
                valueList.put(time_tag, value);
                if (Integer.parseInt(minVar) == 59) {
                    Date maxValueDate = new Date();
                    Double maxValue;
                    if (e == GraphTypeEnum.BZ_GSM_H) {
                        maxValue = valueList.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
                    } else {
                        maxValue = valueList.entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue();
                    }
                    for(Map.Entry<Date, Double> entry : valueList.entrySet()) {
                        Date d = entry.getKey();
                        Double v = entry.getValue();
                        if (v.equals(maxValue)) {
                            maxValueDate = d;
                        }
                    }
                    out.put(maxValueDate, maxValue);
                    valueList = new HashMap<>();
                }
            }
        }
        return out;
    }
}
