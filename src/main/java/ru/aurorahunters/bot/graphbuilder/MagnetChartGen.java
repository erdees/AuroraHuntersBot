package ru.aurorahunters.bot.graphbuilder;

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
import ru.aurorahunters.bot.dao.MagnetometerDAO;
import ru.aurorahunters.bot.enums.MagnetEnum;
import ru.aurorahunters.bot.model.chart.MagnetChart;
import ru.aurorahunters.bot.model.chart.Resolution;
import ru.aurorahunters.bot.utils.GPSUtils;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

public class MagnetChartGen {

    private final Resolution resolution = new Resolution(800, 600);

    /** Throughput method to generate the KEV magnetometer chart  */
    public File getKevChart(String timezone) throws ParseException, SQLException, IOException {
        String name = MagnetEnum.KEV.getDbTableName();
        if (new ChartFileGen().isActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            MagnetChart magnetChart = new MagnetChart(MagnetEnum.KEV, timezone, resolution);
            SortedMap<Date, ArrayList<Double>> values =
                    new MagnetometerDAO().getDailyValues(magnetChart);
            return new ChartFileGen(resolution).getChart(
                    getCombinedChart(magnetChart, values), name);
        }
    }

    /** Throughput method to generate the OUJ magnetometer chart  */
    public File getOujChart(String timezone) throws ParseException, SQLException, IOException {
        String name = MagnetEnum.OUJ.getDbTableName();
        if (new ChartFileGen().isActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            MagnetChart magnetChart = new MagnetChart(MagnetEnum.OUJ, timezone, resolution);
            SortedMap<Date, ArrayList<Double>> values =
                    new MagnetometerDAO().getDailyValues(magnetChart);
            return new ChartFileGen(resolution).getChart(
                    getCombinedChart(magnetChart, values), name);
        }
    }

    /** Throughput method to generate the HAN magnetometer chart  */
    public File getHanChart(String timezone) throws ParseException, SQLException, IOException {
        String name = MagnetEnum.HAN.getDbTableName();
        if (new ChartFileGen().isActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            MagnetChart magnetChart = new MagnetChart(MagnetEnum.HAN, timezone, resolution);
            SortedMap<Date, ArrayList<Double>> values =
                    new MagnetometerDAO().getDailyValues(magnetChart);
            return new ChartFileGen(resolution).getChart(
                    getCombinedChart(magnetChart, values), name);
        }
    }

    /** Throughput method to generate the NUR magnetometer chart  */
    public File getNurChart(String timezone) throws ParseException, SQLException, IOException {
        String name = MagnetEnum.NUR.getDbTableName();
        if (new ChartFileGen().isActual(name)) {
            return new ChartFileGen(resolution).getCachedChart(name);
        } else {
            MagnetChart magnetChart = new MagnetChart(MagnetEnum.NUR, timezone, resolution);
            SortedMap<Date, ArrayList<Double>> values =
                    new MagnetometerDAO().getDailyValues(magnetChart);
            return new ChartFileGen(resolution).getChart(
                    getCombinedChart(magnetChart, values), name);
        }
    }

    /**
     * Method which generates magnetometer chart from a given TreeMap as an argument.
     * @param map optimized data with magnetometer values from db.
     * @return JFreeChart object which can be converted to an image using other methods.
     */
    private JFreeChart getCombinedChart(MagnetChart magnetChart, SortedMap<Date,
            ArrayList<Double>> map) throws ParseException {

        // create subplot X...
        TimeSeriesCollection data_x = new TimeSeriesCollection();
        XYItemRenderer renderer_x = new StandardXYItemRenderer();
        NumberAxis rangeAxis_x = new NumberAxis("Range_X");
        rangeAxis_x.setAutoRangeIncludesZero(false);
        XYPlot subplot_x = new XYPlot(data_x, null, rangeAxis_x, renderer_x);
        applySubPlotStyle(subplot_x);

        // create subplot Y...
        TimeSeriesCollection data_y = new TimeSeriesCollection();
        XYItemRenderer renderer_y = new StandardXYItemRenderer();
        NumberAxis rangeAxis_y = new NumberAxis("Range_Y");
        rangeAxis_y.setAutoRangeIncludesZero(false);
        XYPlot subplot_y = new XYPlot(data_y, null, rangeAxis_y, renderer_y);
        applySubPlotStyle(subplot_y);

        // create subplot Z...
        TimeSeriesCollection data_z = new TimeSeriesCollection();
        XYItemRenderer renderer_z = new StandardXYItemRenderer();
        renderer_z.setSeriesPaint(0, Color.GREEN.brighter());
        NumberAxis rangeAxis_z = new NumberAxis("Range_Z");
        rangeAxis_z.setAutoRangeIncludesZero(false);
        XYPlot subplot_z = new XYPlot(data_z, null, rangeAxis_z, renderer_z);
        applySubPlotStyle(subplot_z);

        // parent plot...
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Domain"));

        // add the subplots...
        plot.add(subplot_z, 1);
        plot.add(subplot_x, 1);
        plot.add(subplot_y, 1);
        ValueAxis domainAxis = new DateAxis();
        plot.setDomainAxis(domainAxis);

        JFreeChart chart = new JFreeChart(magnetChart.getMagnetEnum().getPrintName() + " Magnetometer - last " +
                "24 hours | " +
                "UTC" + magnetChart.getTimezone(),
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        setChartStyle(plot, chart);

        TimeSeries series_x = new TimeSeries("X");
        TimeSeries series_y = new TimeSeries("Y");
        TimeSeries series_z = new TimeSeries("Z");

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
    private void applySubPlotStyle(XYPlot plot) {
        plot.getRangeAxis().setTickLabelPaint(Color.white);
        plot.getRangeAxis().setLabelPaint(Color.white);
        plot.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);
        plot.setBackgroundPaint(new Color(9, 255, 0, 20));
    }

    /** Configure styles, fonts and colors which is the same for all project charts. */
    private void setChartStyle(XYPlot plot, JFreeChart chart) throws ParseException {
        chart.getLegend().setBackgroundPaint(chart.getBackgroundPaint());
        plot.getDomainAxis().setLabel("Finnish Meteorogical Institute | " + Config.getWEBSITE()
                + " Telegram Bot (" + Config.getBotUsername() + ") | " + new GPSUtils().getCurrentTime());
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
}
