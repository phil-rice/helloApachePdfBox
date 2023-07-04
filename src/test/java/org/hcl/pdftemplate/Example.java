package org.hcl.pdftemplate;

import lombok.var;
import org.hcl.pdftemplate.freeChart.ChartBuilder;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hcl.pdftemplate.GraphContents.from;
import static org.hcl.pdftemplate.freeChart.BufferedImageUtils.removeAlphaChannel;


public class Example {
    static Map<String, Object> MapOf(String date, Double value, Double value2) {
        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("value", value);
        result.put("value2", value2);
        return result;
    }
    static List<Map<String, Object>> data =
            Arrays.asList(
                    MapOf("2016-01-01", 567d, 100d),
                    MapOf("2016-01-02", 612d, 700d),
                    MapOf("2016-02-03", 800d, 400d),
                    MapOf("2016-02-04", 980d, 900d),
                    MapOf("2017-04-05", 1410d, 1000d),
                    MapOf("2018-05-06", 2350d, 1200d));
    static RegularTimePeriod dateFromString(Object obj) {
        var s = obj.toString();
        return new Day(Integer.parseInt(s.substring(8, 10)), Integer.parseInt(s.substring(5, 7)), Integer.parseInt(s.substring(0, 4)));
    }


    public static void main(String[] args) throws Exception {
        var dataToFreeChart = ChartBuilder.<List<Map<String, Object>>>forDataChart(data -> "Title").
                subTitle(data -> "SubTitle").
                addSeries("Series1", Color.RED, from("date", "value")).
                addSeries("Series2", Color.BLUE, from("date", "value2")).
                build();

        JFreeChart chart = dataToFreeChart.apply(data);

        BufferedImage image = chart.createBufferedImage(600, 400);
        BufferedImage usableImage = removeAlphaChannel(image, Color.WHITE.getRGB());
        try (OutputStream stream = Files.newOutputStream(Paths.get("chart.jpg"))) {
            ChartUtils.writeBufferedImageAsJPEG(stream, usableImage);
        }
    }
}
