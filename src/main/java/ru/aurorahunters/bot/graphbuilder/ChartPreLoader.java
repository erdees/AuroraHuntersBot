package ru.aurorahunters.bot.graphbuilder;

import ru.aurorahunters.bot.Config;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class ChartPreLoader implements Runnable {

    private static final String DEFAULT_TIMEZONE = Config.getGraphPreloaderTimezone();

    @Override
    public void run() {
        try {
            preloadMagnetometerCharts();
            preloadSunWindCharts();
        } catch (ParseException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void preloadMagnetometerCharts() throws ParseException, SQLException, IOException {
        new MagnetChartGen().getKevChart(DEFAULT_TIMEZONE);
        new MagnetChartGen().getOujChart(DEFAULT_TIMEZONE);
        new MagnetChartGen().getHanChart(DEFAULT_TIMEZONE);
        new MagnetChartGen().getNurChart(DEFAULT_TIMEZONE);
    }

    private void preloadSunWindCharts() throws ParseException, SQLException, IOException {
        new SunWindChartGen().getDensityGraph(DEFAULT_TIMEZONE);
        new SunWindChartGen().getSpeedGraph(DEFAULT_TIMEZONE);
        new SunWindChartGen().getBzGraph(DEFAULT_TIMEZONE);
        new SunWindChartGen().getBtGraph(DEFAULT_TIMEZONE);
    }
}
