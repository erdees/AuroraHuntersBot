package ru.aurorahunters.bot.graphbuilder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.TimeClass;
import ru.aurorahunters.bot.controller.GetDataFromDB;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeGraph {

    public static File getDensityGraph(String timezone) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        String SQL_SELECT_BZ = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone +"' at time zone 'utc', density from data ORDER BY time_tag desc limit 180) SELECT * FROM t ORDER BY timezone ASC";
        PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(SQL_SELECT_BZ);
        ResultSet resultSet = preparedStatement.executeQuery();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | " + Config.getWEBSITE() + " Telegram Bot (" + Config.getBotUsername() + ") | " + TimeClass.GetCurrentGmtTime());

        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double density = resultSet.getDouble(2);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            timeChart.add(new Minute(date), density);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Proton Density [p/cc] - last 3 hours", "Time (UTC" + timezone+") | " + "Waiting time: " + GetDataFromDB.getWaitingTime(), "Proton Density [p/cc]", dataset,
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

    public static File getSpeedGraph(String timezone) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        String SQL_SELECT_BZ = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone +"' at time zone 'utc', speed from data ORDER BY time_tag desc limit 180) SELECT * FROM t ORDER BY timezone ASC;\n";

        PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(SQL_SELECT_BZ);
        ResultSet resultSet = preparedStatement.executeQuery();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | " + Config.getWEBSITE() + " Telegram Bot (" + Config.getBotUsername() + ") | " + TimeClass.GetCurrentGmtTime());

        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double speed = resultSet.getDouble(2);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            timeChart.add(new Minute(date), speed);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Bulk Speed [km/s] - last 3 hours", "Time (UTC" + timezone+") | " + "Waiting time: " + GetDataFromDB.getWaitingTime(), "Bulk Speed [km/s]", dataset,
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

    public static File getBzGraph(String timezone) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        String SQL_SELECT_BZ = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone +"' at time zone 'utc', bz_gsm from data ORDER BY time_tag desc limit 180) SELECT * FROM t ORDER BY timezone ASC;\n";

        PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(SQL_SELECT_BZ);
        ResultSet resultSet = preparedStatement.executeQuery();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | " + Config.getWEBSITE() + " Telegram Bot (" + Config.getBotUsername() + ") | " + TimeClass.GetCurrentGmtTime());

        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double bz_gsm = resultSet.getDouble(2);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            timeChart.add(new Minute(date), bz_gsm);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Bz [nT] - last 3 hours", "Time (UTC" + timezone+") | " + "Waiting time: " + GetDataFromDB.getWaitingTime(), "Bz [nT]", dataset,
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

}
