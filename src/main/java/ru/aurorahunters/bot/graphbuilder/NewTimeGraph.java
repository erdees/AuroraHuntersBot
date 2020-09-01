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

public class NewTimeGraph {

    /** Throughput method to generate a proton density graph  */
    public static File getDensityGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(getGraph(timezone, GraphTypeEnum.DENSITY));
    }

    /** Throughput method to generate a solar speed graph  */
    public static File getSpeedGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(getGraph(timezone, GraphTypeEnum.SPEED));
    }

    /** Throughput method to generate a bz graph  */
    public static File getBzGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(getGraph(timezone, GraphTypeEnum.BZ_GSM));
    }

    /**
     * Method generates a graph according to Database query.
     * @param timezone necessary for query field. Parameter already verified in other class.
     * @param e GraphTypeEnum.
     * @return JFreeChart object e.g. graph which can be placed to a file.
     */
    private static JFreeChart getGraph(String timezone, GraphTypeEnum e) throws SQLException, ParseException {
        String SQL_SELECT = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone +"' at time zone 'utc', "
                + e.dbKey + " from data ORDER BY time_tag desc limit 180) SELECT * FROM t ORDER BY timezone ASC";
        PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(SQL_SELECT);
        ResultSet resultSet = preparedStatement.executeQuery();
        TimeSeries timeChart = new TimeSeries("NOAA DSCOVR | " + Config.getWEBSITE()
                + " Telegram Bot (" + Config.getBotUsername() + ") | " + TimeClass.GetCurrentGmtTime());
        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double value = resultSet.getDouble(2);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            timeChart.add(new Minute(date), value);
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(timeChart);
        // Create the chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                e.printName + " - last 3 hours", "Time (UTC" + timezone + ") | " + "Waiting time: "
                        + GetDataFromDB.getWaitingTime(), e.printName, dataset, true, true, false);
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
        setGraphPlot(plot, e);
        return chart;
    }

    /**
     * Configure Interval Markers, colors and other unique parameters for generating graph.
     * @param plot of XYPlot object.
     * @param e GraphTypeEnum with required for each graph fields.
     */
    private static void setGraphPlot(XYPlot plot, GraphTypeEnum e) {
        if (e == GraphTypeEnum.DENSITY) {
            plot.addRangeMarker(new IntervalMarker(-10,4, getColor(Config.getGraphColorLow())));
            plot.addRangeMarker(new IntervalMarker(4,9, getColor(Config.getGraphColorNormal())));
            plot.addRangeMarker(new IntervalMarker(9,13, getColor(Config.getGraphColorModerate())));
            plot.addRangeMarker(new IntervalMarker(13,18, getColor(Config.getGraphColorHigh())));
            plot.addRangeMarker(new IntervalMarker(18,150, getColor(Config.getGraphColorVeryhigh())));
        }
        if (e == GraphTypeEnum.SPEED) {
            plot.addRangeMarker(new IntervalMarker(0, 400, getColor(Config.getGraphColorLow())));
            plot.addRangeMarker(new IntervalMarker(400, 550, getColor(Config.getGraphColorNormal())));
            plot.addRangeMarker(new IntervalMarker(550, 600, getColor(Config.getGraphColorModerate())));
            plot.addRangeMarker(new IntervalMarker(600, 650, getColor(Config.getGraphColorHigh())));
            plot.addRangeMarker(new IntervalMarker(650, 1000, getColor(Config.getGraphColorVeryhigh())));
        }
        if (e == GraphTypeEnum.BZ_GSM) {
            plot.addRangeMarker(new IntervalMarker(-1.1, 50, getColor(Config.getGraphColorLow())));
            plot.addRangeMarker(new IntervalMarker(-3.2, -1.1, getColor(Config.getGraphColorNormal())));
            plot.addRangeMarker(new IntervalMarker(-4.8, -3.2, getColor(Config.getGraphColorModerate())));
            plot.addRangeMarker(new IntervalMarker(-8.4, -4.8, getColor(Config.getGraphColorHigh())));
            plot.addRangeMarker(new IntervalMarker(-50, -8.4, getColor(Config.getGraphColorVeryhigh())));
        }
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
}
