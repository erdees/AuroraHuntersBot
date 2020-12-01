package ru.aurorahunters.bot.graphbuilder;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.controller.MagnetometerTypeEnum;
import ru.aurorahunters.bot.utils.TimeClass;
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

public class MagnetometerGraph  {

    /** Throughput method to generate the KEV magnetometer chart  */
    public static File getKevMagnetGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(createCombinedMagnetChart(timezone, MagnetometerTypeEnum.KEV,
                getDailyMagnetometerValues(timezone, MagnetometerTypeEnum.KEV)));
    }

    /** Throughput method to generate the OUJ magnetometer chart  */
    public static File getOujMagnetGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(createCombinedMagnetChart(timezone, MagnetometerTypeEnum.OUJ,
                getDailyMagnetometerValues(timezone, MagnetometerTypeEnum.OUJ)));
    }

    /** Throughput method to generate the HAN magnetometer chart  */
    public static File getHanMagnetGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(createCombinedMagnetChart(timezone, MagnetometerTypeEnum.HAN,
                getDailyMagnetometerValues(timezone, MagnetometerTypeEnum.HAN)));
    }

    /** Throughput method to generate the NUR magnetometer chart  */
    public static File getNurMagnetGraph(String timezone) throws ParseException, SQLException, IOException {
        return getGraphFile(createCombinedMagnetChart(timezone, MagnetometerTypeEnum.NUR,
                getDailyMagnetometerValues(timezone, MagnetometerTypeEnum.NUR)));
    }

    /**
     * Method which generates magnetometer chart from a given TreeMap as an argument.
     * @param timezone timezone from MessageHandler to add actual user's timezone to chart.
     * @param e Enum type to detect which magnetometer will be generated.
     * @param map optimized data with magnetometer values from db.
     * @return JFreeChart object which can be converted to an image using other methods.
     */
    private static JFreeChart createCombinedMagnetChart(String timezone, MagnetometerTypeEnum e, TreeMap<Date,
            ArrayList<Double>> map) throws ParseException {

        // create subplot X...
        final TimeSeriesCollection data_x = new TimeSeriesCollection();
        final XYItemRenderer renderer_x = new StandardXYItemRenderer();
        final NumberAxis rangeAxis_x = new NumberAxis("Range_X");
        rangeAxis_x.setAutoRangeIncludesZero(false);
        final XYPlot subplot_x = new XYPlot(data_x, null, rangeAxis_x, renderer_x);
        applySubPlotStyle(subplot_x);

        // create subplot Y...
        final TimeSeriesCollection data_y = new TimeSeriesCollection();
        final XYItemRenderer renderer_y = new StandardXYItemRenderer();
        final NumberAxis rangeAxis_y = new NumberAxis("Range_Y");
        rangeAxis_y.setAutoRangeIncludesZero(false);
        final XYPlot subplot_y = new XYPlot(data_y, null, rangeAxis_y, renderer_y);
        applySubPlotStyle(subplot_y);

        // create subplot Z...
        final TimeSeriesCollection data_z = new TimeSeriesCollection();
        final XYItemRenderer renderer_z = new StandardXYItemRenderer();
        renderer_z.setSeriesPaint(0, Color.GREEN.brighter());
        final NumberAxis rangeAxis_z = new NumberAxis("Range_Z");
        rangeAxis_z.setAutoRangeIncludesZero(false);
        final XYPlot subplot_z = new XYPlot(data_z, null, rangeAxis_z, renderer_z);
        applySubPlotStyle(subplot_z);

        // parent plot...
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Domain"));

        // add the subplots...
        plot.add(subplot_z, 1);
        plot.add(subplot_x, 1);
        plot.add(subplot_y, 1);
        ValueAxis domainAxis = new DateAxis();
        plot.setDomainAxis(domainAxis);

        JFreeChart chart = new JFreeChart(e.getPrintName() + " Magnetometer - last 24 hours | UTC" + timezone,
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        setChartStyle(plot, chart);

        final TimeSeries series_x = new TimeSeries("X");
        final TimeSeries series_y = new TimeSeries("Y");
        final TimeSeries series_z = new TimeSeries("Z");

        for(Map.Entry<Date, ArrayList<Double>> entry : map.entrySet()) {
            Date time_tag = entry.getKey();
            ArrayList<Double> value = entry.getValue();
                if (value.get(0) != 99999.9 || value.get(1) != 99999.9 || value.get(2) != 99999.9) {
                    series_x.add(new Second(time_tag), value.get(0));
                    series_y.add(new Second(time_tag), value.get(1));
                    series_z.add(new Second(time_tag), value.get(2));
                }
        }

        data_x.addSeries(series_x);
        data_y.addSeries(series_y);
        data_z.addSeries(series_z);

        return chart;
    }

    /** Method which set appearance (common settings)for all subplots of combined chart */
    private static void applySubPlotStyle(XYPlot plot) {
        plot.getRangeAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setLabelPaint(Color.white);
        plot.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);
        plot.setBackgroundPaint(new Color(9, 255, 0, 20));
    }

    /** Configure styles, fonts and colors which is the same for all project charts. */
    private static void setChartStyle(XYPlot plot, JFreeChart chart) throws ParseException { ;
        chart.getLegend().setBackgroundPaint(chart.getBackgroundPaint());
        plot.getDomainAxis().setLabel("Finnish Meteorogical Institute | " + Config.getWEBSITE()
                + " Telegram Bot (" + Config.getBotUsername() + ") | " + TimeClass.GetCurrentGmtTime());
        chart.setBackgroundPaint(Color.BLACK);
        chart.getTitle().setPaint(Color.white);
        chart.getLegend().setBackgroundPaint(Color.BLACK);
        chart.getLegend().setItemPaint(Color.white);
        plot.setBackgroundPaint(Color.BLACK);
        plot.getDomainAxis().setTickLabelPaint(Color.white);
        plot.getDomainAxis().setLabelPaint(Color.white);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);
    }

    /**
     * Returns a file with generated graph of JFreeChart object.
     * @param c chart from prepared JFreeChart object.
     * @return a file.
     */
    private static File getGraphFile(JFreeChart c) throws IOException, SQLException, ParseException {
        File file = new File(".png");
        ChartUtils.saveChartAsPNG(file, c, 800, 600);
        return file;
    }

    /**
     * Method which retrieves a HashMap with daily magnetometers data.
     * @param timezone string which will go from MessageHandler
     * @param e TypeEnum to understand which magnetometer we need.
     * @return a Map with daily magnetometers data
     */
    private static TreeMap<Date, ArrayList<Double>> getDailyMagnetometerValues(String timezone, MagnetometerTypeEnum e)
            throws SQLException, ParseException {
        TreeMap<Date, ArrayList<Double>> out = new TreeMap<>();
        String SQL_SELECT = "WITH t AS (SELECT time_tag at time zone 'utc/" + timezone + "' at time zone 'utc', " +
                " mag_x, mag_y, mag_z from " + e.getDbTableName() +
                " ORDER BY time_tag desc limit 8640) SELECT * FROM t ORDER BY timezone ASC";
        PreparedStatement preparedStatement = Config.getDbConnection().prepareStatement(SQL_SELECT);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Timestamp time_tag = resultSet.getTimestamp(1);
            double x = resultSet.getDouble(2);
            double y = resultSet.getDouble(3);
            double z = resultSet.getDouble(4);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_tag.toString());
            out.put(date, new ArrayList<>(Arrays.asList(x, y, z)));
        }
        return out;
    }
}
