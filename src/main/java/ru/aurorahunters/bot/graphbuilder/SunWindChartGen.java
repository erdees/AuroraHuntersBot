package ru.aurorahunters.bot.graphbuilder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.dao.DataDAO;
import ru.aurorahunters.bot.enums.GraphTypeEnum;
import ru.aurorahunters.bot.model.ActualSunChart;
import ru.aurorahunters.bot.model.ArchiveChart;
import ru.aurorahunters.bot.model.Resolution;
import ru.aurorahunters.bot.service.solarwind.ValueCalculator;
import ru.aurorahunters.bot.utils.GPSUtils;
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

public class SunWindChartGen {

    private final Resolution resolution = new Resolution(600, 400);

    /** Throughput method to generate a proton density graph  */
    public File getDensityGraph(String timezone) throws ParseException, SQLException, IOException {
        String name = GraphTypeEnum.DENSITY.getDbKey();
        if (new ChartFileGen().isActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            ActualSunChart chart = new ActualSunChart(GraphTypeEnum.DENSITY, timezone, resolution);
            SortedMap<Date, Double> chartData = new DataDAO().getCurrentChart(chart);
            return new ChartFileGen(chart.getResolution()).
                    getChart(getGraph(chart, chartData), chart.getGraphTypeEnum().getDbKey());
        }
    }

    /** Throughput method to generate a solar speed graph  */
    public File getSpeedGraph(String timezone) throws ParseException, SQLException, IOException {
        String name = GraphTypeEnum.SPEED.getDbKey();
        if (new ChartFileGen().isActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            ActualSunChart chart = new ActualSunChart(GraphTypeEnum.SPEED, timezone, resolution);
            SortedMap<Date, Double> chartData = new DataDAO().getCurrentChart(chart);
            return new ChartFileGen(chart.getResolution()).
                    getChart(getGraph(chart, chartData), chart.getGraphTypeEnum().getDbKey());
        }
    }

    /** Throughput method to generate a bz graph  */
    public File getBzGraph(String timezone) throws ParseException, SQLException, IOException {
        String name = GraphTypeEnum.BZ_GSM.getDbKey();
        if (new ChartFileGen().isActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            ActualSunChart chart = new ActualSunChart(GraphTypeEnum.BZ_GSM, timezone, resolution);
            SortedMap<Date, Double> chartData = new DataDAO().getCurrentChart(chart);
            return new ChartFileGen(chart.getResolution()).
                    getChart(getGraph(chart, chartData), chart.getGraphTypeEnum().getDbKey());
        }
    }

    /** Throughput method to generate a bt graph  */
    public File getBtGraph(String timezone) throws ParseException, SQLException, IOException {
        String name = GraphTypeEnum.BT.getDbKey();
        if (new ChartFileGen().isActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            ActualSunChart chart = new ActualSunChart(GraphTypeEnum.BT, timezone, resolution);
            SortedMap<Date, Double> chartData = new DataDAO().getCurrentChart(chart);
            return new ChartFileGen(chart.getResolution()).
                    getChart(getGraph(chart, chartData), chart.getGraphTypeEnum().getDbKey());
        }
    }

    /** Throughput method to generate an archive proton density graph  */
    public File getDensityArchiveGraph(String date) throws ParseException, SQLException, IOException {
        String name = GraphTypeEnum.DENSITY_H.getDbKey() + "_" + date;
        if (new ChartFileGen().isArchiveActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            ArchiveChart archiveChart = new ArchiveChart(GraphTypeEnum.DENSITY_H, resolution, date);
            TreeMap<Date, Double> chartData = getArchiveValues(archiveChart);
            return new ChartFileGen(archiveChart.getResolution()).
                    getChart(getGraph(archiveChart, chartData), name);
        }
    }

    /** Throughput method to generate an archive solar speed graph  */
    public File getSpeedArchiveGraph(String date) throws ParseException, SQLException, IOException {
        String name = GraphTypeEnum.SPEED_H.getDbKey() + "_" + date;
        if (new ChartFileGen().isArchiveActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            ArchiveChart archiveChart = new ArchiveChart(GraphTypeEnum.SPEED_H, resolution, date);
            TreeMap<Date, Double> chartData = getArchiveValues(archiveChart);
            return new ChartFileGen(archiveChart.getResolution()).
                    getChart(getGraph(archiveChart, chartData), name);
        }
    }

    /** Throughput method to generate an archive bz graph  */
    public File getBzArchiveGraph(String date) throws ParseException, SQLException, IOException {
        String name = GraphTypeEnum.BZ_GSM_H.getDbKey() + "_" + date;
        if (new ChartFileGen().isArchiveActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            ArchiveChart archiveChart = new ArchiveChart(GraphTypeEnum.BZ_GSM_H, resolution, date);
            TreeMap<Date, Double> chartData = getArchiveValues(archiveChart);
            return new ChartFileGen(archiveChart.getResolution()).
                    getChart(getGraph(archiveChart, chartData), name);
        }
    }

    /**
     * Method generates a graph according to received as an argument TreeMap.
     * @param m is Map with necessary for chart generation values.
     * @return JFreeChart object e.g. graph which can be placed to a file.
     */
    private JFreeChart getGraph(ActualSunChart graph, SortedMap<Date, Double> m)
            throws SQLException, ParseException {
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | " + Config.getWEBSITE()
                + " Telegram Bot (" + Config.getBotUsername() + ") | " + new GPSUtils().getCurrentTime());
        for(Map.Entry<Date, Double> entry : m.entrySet()) {
            Date timeTag = entry.getKey();
            Double value = entry.getValue();
            timeChart.add(new Minute(timeTag), value);
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);
        JFreeChart chart = getChartByType(graph.getTimezone(), graph.getGraphTypeEnum(), dataset);
        XYPlot plot = chart.getXYPlot();
        setChartStyle(plot, chart);
        setRangeAndColor(plot, graph.getGraphTypeEnum());
        return chart;
    }

    /**
     * Method generates a graph according to received as an argument TreeMap.
     * @param m is Map with necessary for chart generation values.
     * @return JFreeChart object e.g. graph which can be placed to a file.
     */
    private JFreeChart getGraph(ArchiveChart graph, TreeMap<Date, Double> m)
            throws SQLException, ParseException {
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | " + Config.getWEBSITE()
                + " Telegram Bot (" + Config.getBotUsername() + ") | " + new GPSUtils().getCurrentTime());
        for(Map.Entry<Date, Double> entry : m.entrySet()) {
            Date timeTag = entry.getKey();
            Double value = entry.getValue();
            timeChart.add(new Minute(timeTag), value);
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);
        JFreeChart chart = getChartByType(graph.getDate(), graph.getGraphTypeEnum(), dataset);
        XYPlot plot = chart.getXYPlot();
        setChartStyle(plot, chart);
        setRangeAndColor(plot, graph.getGraphTypeEnum());
        return chart;
    }

    /**
     * This method creates the chart with necessary labels according to chart type (last or history).
     * @param timezoneOrDate which is timezone or archive date which depend on throughput methods.
     * @param e GraphTypeEnum which required to detect chart type.
     * @param d created earlier TimeSeriesCollection.
     * @return created JFreeChart object.
     */
    private JFreeChart getChartByType(String timezoneOrDate, GraphTypeEnum e, TimeSeriesCollection d)
            throws SQLException {
        JFreeChart chart;
        if (e == GraphTypeEnum.BZ_GSM || e == GraphTypeEnum.SPEED
                || e == GraphTypeEnum.DENSITY || e == GraphTypeEnum.BT) {
            chart = ChartFactory.createTimeSeriesChart(
                    e.getPrintName() + " - last 3 hours",
                    "Time (UTC" + timezoneOrDate + ") | " + "Waiting time: " +
                            new ValueCalculator().getWaitingTime(),
                    e.getPrintName(), d, true, true, false);
        } else {
            chart = ChartFactory.createTimeSeriesChart(
                    e.getPrintName() + " | Archive for " + timezoneOrDate,
                    "Time (UTC+00:00)" , e.getPrintName(), d, true, true, false);
        }
        return chart;
    }

    /**
     * Configure Interval Markers, colors and other unique parameters for generating graph.
     * @param plot of XYPlot object.
     * @param e GraphTypeEnum with required for each graph fields.
     */
    private void setRangeAndColor(XYPlot plot, GraphTypeEnum e) {
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
        if (e == GraphTypeEnum.BT) {
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBtQuietStart(),
                    Config.getGraphRangeBtQuietEnd(), getColor(Config.getGraphColorQuiet())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBtModerateStart(),
                    Config.getGraphRangeBtModerateEnd(), getColor(Config.getGraphColorModerate())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBtIncreasedStart(),
                    Config.getGraphRangeBtIncreasedEnd(), getColor(Config.getGraphColorIncreased())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBtHighStart(),
                    Config.getGraphRangeBtHighEnd(), getColor(Config.getGraphColorHigh())));
            plot.addRangeMarker(new IntervalMarker(Config.getGraphRangeBtExtremeStart(),
                    Config.getGraphRangeBtExtremeEnd(), getColor(Config.getGraphColorExtreme())));
        }
    }

    /** Configure styles, fonts and colors which is the same for all solar wind charts. */
    private void setChartStyle(XYPlot plot, JFreeChart chart) {
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
    private Color getColor(String color) {
        String[] rgba;
        rgba = color.split(",");
        int r = Integer.parseInt(rgba[0].trim());
        int g = Integer.parseInt(rgba[1].trim());
        int b = Integer.parseInt(rgba[2].trim());
        int a = Integer.parseInt(rgba[3].trim());
        return new Color(r,g,b,a);
    }

    /**
     * Method which retrieves from a Database values for chart generation in "archive" mode according to user request.
     * @return TreeMap with Date and Double values which is datasource for final chart generation.
     */
    private TreeMap<Date, Double> getArchiveValues(ArchiveChart archiveChart)
    //TODO: get rid of this method and refactor it
            throws SQLException, ParseException {
        TreeMap<Date, Double> out = new TreeMap<>();
        HashMap<Date, Double> valueList = new HashMap<>();
        SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd");
        if (archiveChart.getDate().matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")) {
            Date indicatedDay = formattedDate.parse(archiveChart.getDate());
            Calendar c = Calendar.getInstance();
            c.setTime(indicatedDay);
            c.add(Calendar.DATE, 1);
            Date nextDay = c.getTime();
            String initialDay = formattedDate.format(indicatedDay);
            String finalDay = formattedDate.format(nextDay);
            String SQL_TEST =
                    "SELECT time_tag, " + archiveChart.getGraphTypeEnum().getDbKey() + " FROM data WHERE " +
                            "time_tag >= '" + initialDay +
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
                    if (archiveChart.getGraphTypeEnum() == GraphTypeEnum.BZ_GSM_H) {
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
