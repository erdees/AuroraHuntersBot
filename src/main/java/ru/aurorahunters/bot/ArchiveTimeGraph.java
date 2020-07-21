package ru.aurorahunters.bot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
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

public class ArchiveTimeGraph {
    private static String initialDay;
    private static String finalDay;
    private static final String DENSITY = "density";
    private static final String SPEED = "speed";
    private static final String BZ_GSM = "bz_gsm";

    public static File getDensityGraph(String date) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        Iterator it = getHistoryValues(date, DENSITY).entrySet().iterator();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | auroralights.ru Telegram Bot (@bot_name) | archive for " + date);
        while (it.hasNext()) {
            HashMap.Entry<Integer, Double> pair = (HashMap.Entry)it.next();
            double density = pair.getValue();
            Date graphDate = new SimpleDateFormat("H").parse(pair.getKey().toString());
            timeChart.add(new Hour(graphDate), density);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Proton Density [p/cc] - archive for " + date, "Time (UTC)", "Proton Density [p/cc]", dataset,
                true, true, false);

        XYPlot plot = chart.getXYPlot();

        final Marker one = new ValueMarker(4);
        one.setPaint(Color.yellow);
        one.setLabel("increased");
        one.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        one.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(one);

        final Marker two = new ValueMarker(9);
        two.setPaint(Color.orange);
        two.setLabel("moderate");
        two.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        two.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(two);

        final Marker three = new ValueMarker(13);
        three.setPaint(Color.red.brighter());
        three.setLabel("high");
        three.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        three.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(three);

        final Marker four = new ValueMarker(18);
        four.setPaint(Color.red);
        four.setLabel("very high");
        four.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        four.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(four);

        chart.setBackgroundPaint(Color.BLACK);
        chart.getTitle().setPaint(Color.white);
        chart.getLegend().setBackgroundPaint(Color.BLACK);
        chart.getLegend().setItemPaint(Color.white);

        plot.setBackgroundPaint(Color.BLACK);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.white);
        plot.getDomainAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setRange(1, 99);
        plot.getDomainAxis().setLabelPaint(Color.white);
        plot.getRangeAxis().setLabelPaint(Color.white);

        //Save chart as PNG
        ChartUtilities.saveChartAsPNG(file, chart, 600, 400);
        return file;
    }

    public static File getSpeedGraph(String date) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        Iterator it = getHistoryValues(date, SPEED).entrySet().iterator();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | auroralights.ru Telegram Bot (@bot_name) | archive for " + date);
        while (it.hasNext()) {
            HashMap.Entry<Integer, Double> pair = (HashMap.Entry)it.next();
            double speed = pair.getValue();
            Date graphDate = new SimpleDateFormat("H").parse(pair.getKey().toString());
            timeChart.add(new Hour(graphDate), speed);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Bulk Speed [km/s] - archive for " + date, "Time (UTC)", "Bulk Speed [km/s]", dataset,
                true, true, false);

        XYPlot plot = chart.getXYPlot();

        final Marker one = new ValueMarker(400);
        one.setPaint(Color.yellow);
        one.setLabel("increased");
        one.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        one.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(one);

        final Marker two = new ValueMarker(550);
        two.setPaint(Color.orange);
        two.setLabel("moderate");
        two.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        two.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(two);

        final Marker three = new ValueMarker(600);
        three.setPaint(Color.red.brighter());
        three.setLabel("high");
        three.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        three.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(three);

        final Marker four = new ValueMarker(650);
        four.setPaint(Color.red);
        four.setLabel("very high");
        four.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        four.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(four);

        chart.setBackgroundPaint(Color.BLACK);
        chart.getTitle().setPaint(Color.white);
        chart.getLegend().setBackgroundPaint(Color.BLACK);
        chart.getLegend().setItemPaint(Color.white);

        plot.setBackgroundPaint(Color.BLACK);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.white);
        plot.getDomainAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setRange(100, 999);
        plot.getDomainAxis().setLabelPaint(Color.white);
        plot.getRangeAxis().setLabelPaint(Color.white);

        //Save chart as PNG
        ChartUtilities.saveChartAsPNG(file, chart, 600, 400);
        return file;
    }

    public static File getBzGraph(String date) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        Iterator it = getHistoryValues(date, BZ_GSM).entrySet().iterator();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | auroralights.ru Telegram Bot (@bot_name) | archive for " + date);
        while (it.hasNext()) {
            HashMap.Entry<Integer, Double> pair = (HashMap.Entry)it.next();
            double bz = pair.getValue();
            Date graphDate = new SimpleDateFormat("H").parse(pair.getKey().toString());
            timeChart.add(new Hour(graphDate), bz);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Bz [nT] - archive for " + date, "Time (UTC)", "Bz [nT]", dataset,
                true, true, false);

        XYPlot plot = chart.getXYPlot();

        final Marker one = new ValueMarker(-1.1);
        one.setPaint(Color.yellow);
        one.setLabel("increased");
        one.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        one.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(one);

        final Marker two = new ValueMarker(-3.2);
        two.setPaint(Color.orange);
        two.setLabel("moderate");
        two.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        two.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(two);

        final Marker three = new ValueMarker(-4.8);
        three.setPaint(Color.red.brighter());
        three.setLabel("high");
        three.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        three.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(three);

        final Marker four = new ValueMarker(-8.4);
        four.setPaint(Color.red);
        four.setLabel("very high");
        four.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        four.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(four);

        chart.setBackgroundPaint(Color.BLACK);
        chart.getTitle().setPaint(Color.white);
        chart.getLegend().setBackgroundPaint(Color.BLACK);
        chart.getLegend().setItemPaint(Color.white);

        plot.setBackgroundPaint(Color.BLACK);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.white);
        plot.getDomainAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setRange(-10, 6);
        plot.getDomainAxis().setLabelPaint(Color.white);
        plot.getRangeAxis().setLabelPaint(Color.white);

        //Save chart as PNG
        ChartUtilities.saveChartAsPNG(file, chart, 600, 400);
        return file;
    }

    private static HashMap<Integer, Double> getHistoryValues(String date, String valueType) throws SQLException, ParseException {
        HashMap<Integer, Double> map = new HashMap<>();
        ArrayList<Double> list = new ArrayList<>();
        if (date.matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")) {
            Date day = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Date nextDay;
            Calendar c = Calendar.getInstance();
            c.setTime(day);
            c.add(Calendar.DATE, 1);
            nextDay = c.getTime();
            initialDay = new SimpleDateFormat("yyyy-MM-dd").format(day);
            finalDay = new SimpleDateFormat("yyyy-MM-dd").format(nextDay);
            final String SQL_TEST = "SELECT time_tag, " + valueType + " FROM data WHERE time_tag >= \'" + initialDay +
                    "\' AND time_tag < \'" + finalDay + "\';";
            PreparedStatement preparedStatement = DBconnection.getConnection().prepareStatement(SQL_TEST);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (valueType.equals("bz_gsm")) {
                while (resultSet.next()) {
                    Timestamp time_tag = resultSet.getTimestamp(1);
                    double dbValue = resultSet.getDouble(2);
                    Date sqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
                    String hourVar = new SimpleDateFormat("HH").format(sqlDate);
                    String minVar = new SimpleDateFormat("mm").format(sqlDate);
                    list.add(dbValue);
                    if (Integer.parseInt(minVar) == 59) {
                        Double max = Collections.min(list);
                        map.put(Integer.parseInt(hourVar), max);
                        list.removeAll(list);
                    }
                }
            } else {
                while (resultSet.next()) {
                    Timestamp time_tag = resultSet.getTimestamp(1);
                    double dbValue = resultSet.getDouble(2);
                    Date sqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
                    String hourVar = new SimpleDateFormat("HH").format(sqlDate);
                    String minVar = new SimpleDateFormat("mm").format(sqlDate);
                    list.add(dbValue);
                    if (Integer.parseInt(minVar) == 59) {
                        Double max = Collections.max(list);
                        map.put(Integer.parseInt(hourVar), max);
                        list.removeAll(list);
                    }
                }
            }
            if (map.isEmpty()) {
                System.out.println("Please check your input and try again.");
            } else {
                return map;
            }
        } else {
            System.out.println("Wrong format. The date should be yyyy-MM-dd, e.g. 2020-07-01 ");
        }

        return null;
    }
}
