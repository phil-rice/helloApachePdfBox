package org.hcl.pdftemplate;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.hcl.pdftemplate.IPdfPrinter.updateTemplate;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest {

    File outputPdf = new File("output.pdf");

    static BufferedImage makeImage() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, 100, 100);
        return image;
    }

    static XYDataset createDataset() {

        XYSeries series = new XYSeries("2016");
        series.add(18, 567);
        series.add(20, 612);
        series.add(25, 800);
        series.add(30, 980);
        series.add(40, 1410);
        series.add(50, 2350);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }

    static TimeSeriesCollection createTimeSeries() {
        TimeSeries series = new TimeSeries("2016");
        series.add(new Day(1, 1, 2016), 567);
        series.add(new Day(2, 1, 2016), 612);
        series.add(new Day(3, 2, 2016), 800);
        series.add(new Day(4, 2, 2016), 980);
        series.add(new Day(5, 4, 2017), 1410);
        series.add(new Day(6, 5, 2018), 2350);
        return new TimeSeriesCollection(series);
    }

    //    static public JFreeChart setupTime(TimeSeries series) {
//
//
//    }
    static public JFreeChart setupXY(TimeSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Average salary per age",
                "Age",
                "Salary (€)",
                dataset
        );
        chart.addSubtitle(new TextTitle("Source: www.jfreechart.com", new Font("Serif", Font.PLAIN, 12)));

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

//        plot.setDomainGridlinesVisible(true);
//        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Average Salary per Age", new Font("Serif", java.awt.Font.BOLD, 18)));
        chart.setSubtitles(Arrays.asList(new TextTitle("Phils subtitle", new Font("Serif", Font.PLAIN,
                12))));

        return chart;
    }


    List<IPdfPart<String>> parts = PdfBuilder.<String>builder()
            .addText(400, 200, "Hello World as text")
            .fontSize(8)
            .addText(400, 150, "Second text")
            .addJfreeChart(100, 0, setupXY(createTimeSeries()))
            .addBufferedImage(100, 200, makeImage())
            .addImage(100, 400, doc -> PDImageXObject.createFromFileByContent(
                    new File(ClassLoader.getSystemResource("picture.jpg").toURI()), doc))
            .build();


    @Test
    public void testMakeFile() throws Exception {
        //OK this isn't a great test. We really want to check we've adding the text and the images...
        //But it's a start: we check the file was created and that no exceptions were thrown.
        outputPdf.delete();
        updateTemplate("/test.pdf", "someData", parts, doc -> doc.save("output.pdf"));
        assertTrue(outputPdf.exists());

    }

}
