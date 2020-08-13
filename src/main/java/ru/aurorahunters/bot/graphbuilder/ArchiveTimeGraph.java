package ru.aurorahunters.bot.graphbuilder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.TimeClass;
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
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | auroralights.ru Telegram Bot (@aurorahunters_bot) | " + TimeClass.getCurrentTime());
        while (it.hasNext()) {
            HashMap.Entry<Integer, Double> pair = (HashMap.Entry)it.next();
            double density = pair.getValue();
            Date graphDate = new SimpleDateFormat("H").parse(pair.getKey().toString());
            timeChart.add(new Hour(graphDate), density);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Proton Density [p/cc] | Archive for " + date, "Time (UTC+00:00)", "Proton Density [p/cc]", dataset,
                true, true, false);

        XYPlot plot = chart.getXYPlot();

        chart.setBackgroundPaint(Color.BLACK);
        chart.getTitle().setPaint(Color.white);
        chart.getLegend().setBackgroundPaint(Color.BLACK);
        chart.getLegend().setItemPaint(Color.white);

        plot.setBackgroundPaint(Color.BLACK);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.white);
        plot.getDomainAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setTickLabelPaint(Color.white);
        //plot.getRangeAxis().setRange(-5, 99);
        plot.getDomainAxis().setLabelPaint(Color.white);
        plot.getRangeAxis().setLabelPaint(Color.white);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);

        plot.addRangeMarker(new IntervalMarker(-10,4, new Color(29, 255,0, 20)));
        plot.addRangeMarker(new IntervalMarker(4,9, new Color(255,216,0,50)));
        plot.addRangeMarker(new IntervalMarker(9,13, new Color(255,102,0,100)));
        plot.addRangeMarker(new IntervalMarker(13,18, new Color(255,0,0,100)));
        plot.addRangeMarker(new IntervalMarker(18,150, new Color(255,0,222,80)));


        //Save chart as PNG
        ChartUtils.saveChartAsPNG(file, chart, 600, 400);
        return file;
    }

    public static File getSpeedGraph(String date) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        Iterator it = getHistoryValues(date, SPEED).entrySet().iterator();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | auroralights.ru Telegram Bot (@aurorahunters_bot) | " + TimeClass.getCurrentTime());
        while (it.hasNext()) {
            HashMap.Entry<Integer, Double> pair = (HashMap.Entry)it.next();
            double speed = pair.getValue();
            Date graphDate = new SimpleDateFormat("H").parse(pair.getKey().toString());
            timeChart.add(new Hour(graphDate), speed);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Bulk Speed [km/s] | Archive for " + date, "Time (UTC+00:00)", "Bulk Speed [km/s]", dataset,
                true, true, false);

        XYPlot plot = chart.getXYPlot();

        chart.setBackgroundPaint(Color.BLACK);
        chart.getTitle().setPaint(Color.white);
        chart.getLegend().setBackgroundPaint(Color.BLACK);
        chart.getLegend().setItemPaint(Color.white);

        plot.setBackgroundPaint(Color.BLACK);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.white);
        plot.getDomainAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setTickLabelPaint(Color.white);
        //plot.getRangeAxis().setRange(100, 999);
        plot.getDomainAxis().setLabelPaint(Color.white);
        plot.getRangeAxis().setLabelPaint(Color.white);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);

        plot.addRangeMarker(new IntervalMarker(0,400, new Color(29, 255,0, 20)));
        plot.addRangeMarker(new IntervalMarker(400,550, new Color(255,216,0,50)));
        plot.addRangeMarker(new IntervalMarker(550,600, new Color(255, 102,0,100)));
        plot.addRangeMarker(new IntervalMarker(600,650, new Color(255,0,0,100)));
        plot.addRangeMarker(new IntervalMarker(650,1000, new Color(255,0,222,80)));

        //Save chart as PNG
        ChartUtils.saveChartAsPNG(file, chart, 600, 400);
        return file;
    }

    public static File getBzGraph(String date) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        Iterator it = getHistoryValues(date, BZ_GSM).entrySet().iterator();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | auroralights.ru Telegram Bot (@aurorahunters_bot) | " + TimeClass.getCurrentTime());
        while (it.hasNext()) {
            HashMap.Entry<Integer, Double> pair = (HashMap.Entry)it.next();
            double bz = pair.getValue();
            Date graphDate = new SimpleDateFormat("H").parse(pair.getKey().toString());
            timeChart.add(new Hour(graphDate), bz);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Bz [nT] | Archive for " + date, "Time (UTC+00:00)", "Bz [nT]", dataset,
                true, true, false);

        XYPlot plot = chart.getXYPlot();

        chart.setBackgroundPaint(Color.BLACK);
        chart.getTitle().setPaint(Color.white);
        chart.getLegend().setBackgroundPaint(Color.BLACK);
        chart.getLegend().setItemPaint(Color.white);

        plot.setBackgroundPaint(Color.BLACK);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.white);
        plot.getDomainAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setTickLabelPaint(Color.white);
        //plot.getRangeAxis().setRange(-10, 8);
        plot.getDomainAxis().setLabelPaint(Color.white);
        plot.getRangeAxis().setLabelPaint(Color.white);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);

        plot.addRangeMarker(new IntervalMarker(-1.1,50, new Color(29, 255,0, 20)));
        plot.addRangeMarker(new IntervalMarker(-3.2,-1.1, new Color(255,216,0,50)));
        plot.addRangeMarker(new IntervalMarker(-4.8,-3.2, new Color(255,102,0,100)));
        plot.addRangeMarker(new IntervalMarker(-8.4,-4.8, new Color(255,0,0,100)));
        plot.addRangeMarker(new IntervalMarker(-50,-8.4, new Color(255,0,222,80)));

        //Save chart as PNG
        ChartUtils.saveChartAsPNG(file, chart, 600, 400);
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
            PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(SQL_TEST);
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
