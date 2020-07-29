package ru.aurorahunters.bot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.time.*;
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
        String SQL_SELECT_BZ = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone +"' at time zone 'utc', density from data ORDER BY time_tag desc limit 180) SELECT * FROM t ORDER BY timezone ASC;\n";

        PreparedStatement preparedStatement = DBconnection.getConnection().prepareStatement(SQL_SELECT_BZ);
        ResultSet resultSet = preparedStatement.executeQuery();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | auroralights.ru Telegram Bot (@aurorahunters_bot) | " + new Date().toString());

        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double density = resultSet.getDouble(2);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            //String newString = new SimpleDateFormat("hh.mm").format(date);
            timeChart.add(new Minute(date), density);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Proton Density [p/cc] - last 3 hours", "Time (UTC)", "Proton Density [p/cc]", dataset,
                true, true, false);

        XYPlot plot = chart.getXYPlot();

       /* chart.addSubtitle(new TextTitle("NOAA DSCOVR | auroralights.ru Telegram Bot (@aurorahunters_bot) | " + new Date().toString(),
                new Font("Dialog", Font.ITALIC, 14), Color.white,
                RectangleEdge.BOTTOM, HorizontalAlignment.CENTER,
                VerticalAlignment.BOTTOM, RectangleInsets.ZERO_INSETS));

        */

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
        ChartUtils.saveChartAsPNG(file, chart, 600, 400);
        return file;
    }

    public static File getSpeedGraph(String timezone) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        String SQL_SELECT_BZ = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone +"' at time zone 'utc', speed from data ORDER BY time_tag desc limit 180) SELECT * FROM t ORDER BY timezone ASC;\n";

        PreparedStatement preparedStatement = DBconnection.getConnection().prepareStatement(SQL_SELECT_BZ);
        ResultSet resultSet = preparedStatement.executeQuery();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | auroralights.ru Telegram Bot (@aurorahunters_bot) | " + new Date().toString());

        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double speed = resultSet.getDouble(2);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            //String newString = new SimpleDateFormat("hh.mm").format(date);
            timeChart.add(new Minute(date), speed);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Bulk Speed [km/s] - last 3 hours", "Time (UTC)", "Bulk Speed [km/s]", dataset,
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
        ChartUtils.saveChartAsPNG(file, chart, 600, 400);
        return file;
    }

    public static File getBzGraph(String timezone) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        String SQL_SELECT_BZ = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone +"' at time zone 'utc', bz_gsm from data ORDER BY time_tag desc limit 180) SELECT * FROM t ORDER BY timezone ASC;\n";

        PreparedStatement preparedStatement = DBconnection.getConnection().prepareStatement(SQL_SELECT_BZ);
        ResultSet resultSet = preparedStatement.executeQuery();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | auroralights.ru Telegram Bot (@aurorahunters_bot) | " + new Date().toString());

        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double bz_gsm = resultSet.getDouble(2);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            //String newString = new SimpleDateFormat("hh.mm").format(date);
            timeChart.add(new Minute(date), bz_gsm);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);

        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Bz [nT] - last 3 hours", "Time (UTC)", "Bz [nT]", dataset,
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
        ChartUtils.saveChartAsPNG(file, chart, 600, 400);
        return file;
    }
}
