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

        // create subplot 1...
        final TimeSeriesCollection data1 = new TimeSeriesCollection();
        final XYItemRenderer renderer1 = new StandardXYItemRenderer();
        final NumberAxis rangeAxis1 = new NumberAxis("nT");
        rangeAxis1.setAutoRangeIncludesZero(false);
        final XYPlot subplot1 = new XYPlot(data1, null, rangeAxis1, renderer1);

        // create subplot 2...
        final TimeSeriesCollection data2 = new TimeSeriesCollection();
        final XYItemRenderer renderer2 = new StandardXYItemRenderer();
        final NumberAxis rangeAxis2 = new NumberAxis("nT");
        rangeAxis2.setAutoRangeIncludesZero(false);
        final XYPlot subplot2 = new XYPlot(data2, null, rangeAxis2, renderer2);

        // create subplot 3...
        final TimeSeriesCollection data3 = new TimeSeriesCollection();
        final XYItemRenderer renderer3 = new StandardXYItemRenderer();
        final NumberAxis rangeAxis3 = new NumberAxis("nT");
        rangeAxis3.setAutoRangeIncludesZero(false);
        final XYPlot subplot3 = new XYPlot(data3, null, rangeAxis3, renderer3);

        // parent plot...
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Domain"));

        // add the subplots...
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        ValueAxis domainAxis = new DateAxis();
        plot.setDomainAxis(domainAxis);

        JFreeChart chart = new JFreeChart(e.getPrintName() + " Magnetometer - last 24 hours | UTC" + timezone,
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        setChartStyle(plot, chart);

        final TimeSeries series1 = new TimeSeries("X");
        final TimeSeries series2 = new TimeSeries("Y");
        final TimeSeries series3 = new TimeSeries("Z");

        for(Map.Entry<Date, ArrayList<Double>> entry : map.entrySet()) {
            Date time_tag = entry.getKey();
            ArrayList<Double> value = entry.getValue();
            series1.add(new Second(time_tag), value.get(0));
            series2.add(new Second(time_tag), value.get(1));
            series3.add(new Second(time_tag), value.get(2));
        }

        data1.addSeries(series1);
        data2.addSeries(series2);
        data3.addSeries(series3);

        return chart;
    }

    /** Configure styles, fonts and colors which is the same for all project charts. */
    private static void setChartStyle(XYPlot plot, JFreeChart chart) throws ParseException { ;
        chart.getLegend().setBackgroundPaint(chart.getBackgroundPaint());
        plot.getDomainAxis().setLabel("Finnish Meteorogical Institute | " + Config.getWEBSITE()
                + " Telegram Bot (" + Config.getBotUsername() + ") | " + TimeClass.GetCurrentGmtTime());
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
