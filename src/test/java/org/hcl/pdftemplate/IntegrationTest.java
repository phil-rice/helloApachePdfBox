package org.hcl.pdftemplate;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

    static public JFreeChart setup(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Average salary per age",
                "Age",
                "Salary (€)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Average Salary per Age",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

        return chart;
    }


    List<IPdfPart> parts = PdfBuilder.builder()
            .addText(400, 200, "Hello World as text")
            .addText(400, 300, "Second text")
            .addJfreeChart(100, 0, setup(createDataset()))
            .addBufferedImage(100, 200, makeImage())
            .addImage(100, 400, doc -> PDImageXObject.createFromFileByContent(new File(ClassLoader.getSystemResource("picture.jpg").toURI()), doc))
            .build();


    @Test
    public void testMakeFile() throws Exception {
        //OK this isn't a great test. We really want to check we've adding the text and the images...
        //But it's a start: we check the file was created and that no exceptions were thrown.
        outputPdf.delete();
        updateTemplate("/test.pdf", parts, doc -> doc.save("output.pdf"));
        assertTrue(outputPdf.exists());

    }

}
