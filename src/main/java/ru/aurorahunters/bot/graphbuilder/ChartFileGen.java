package ru.aurorahunters.bot.graphbuilder;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import ru.aurorahunters.bot.Config;
import ru.aurorahunters.bot.model.chart.Resolution;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;

public class ChartFileGen {

    private Resolution resolution;
    private static final String CACHE_PATH = Config.getGraphPreloaderFolder();

    public ChartFileGen(Resolution resolution) {
        this.resolution = resolution;
    }

    public ChartFileGen() {
    }

    /**
     * Returns a file with generated graph of JFreeChart object.
     * @param chart chart from prepared JFreeChart object.
     * @return a file.
     */
    public File getChart(JFreeChart chart, String name) throws IOException {
        Files.createDirectories(Paths.get(CACHE_PATH));
        String filePath = CACHE_PATH + "." + name + ".png";
        File file = new File(filePath);
        ChartUtils.saveChartAsPNG(file, chart, resolution.getWidth(), resolution.getHeight());
        return file;
    }

    public File getCachedChart(String name) throws IOException {
        Files.createDirectories(Paths.get(CACHE_PATH));
        String filePath = CACHE_PATH + "." + name + ".png";
        return new File(filePath);
    }

    public boolean isActual(String name) throws IOException {
        String path = CACHE_PATH + "." + name + ".png";
        long diffInMinutes;
        if (new File(path).exists()) {
            try {
                diffInMinutes = getTimeDifference(path);
            } catch (NoSuchFileException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        return diffInMinutes < 1;
    }

    public boolean isArchiveActual(String name) {
        String path = CACHE_PATH + "." + name + ".png";
        return new File(path).exists();
    }

    private long getTimeDifference(String path) throws IOException {
        BasicFileAttributes attributes;
        Instant lastModified;
        long diffInMins;
        attributes =
                Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
        lastModified = attributes.lastModifiedTime().toInstant();
        diffInMins = Duration.between(lastModified, Instant.now()).toMinutes();
        return diffInMins;
    }
}
