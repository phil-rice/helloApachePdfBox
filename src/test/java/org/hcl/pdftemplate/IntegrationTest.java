package org.hcl.pdftemplate;

import lombok.var;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.hcl.pdftemplate.freeChart.ChartBuilder;
import org.jfree.data.time.RegularTimePeriod;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;import java.util.List;
import java.util.Map;

import static org.hcl.pdftemplate.Example.from;
import static org.hcl.pdftemplate.IPdfPrinter.processTemplate;
import static org.hcl.pdftemplate.IPdfPrinter.processTemplateAndReturn;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest {

    File outputPdf = new File("output.pdf");

    static BufferedImage makeImage() {
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, 50, 50);
        return image;
    }

    private static ChartBuilder<List<Map<String, Object>>, RegularTimePeriod> chartBuilder(String title, String yAxis) {
        return ChartBuilder.forDataChart(title, yAxis);
    }

    List<IPdfPart<List<Map<String, Object>>>> parts = PdfBuilder.<List<Map<String, Object>>>builder()
            .addImage(100, 450, (doc, data) -> PDImageXObject.createFromFileByContent(
                    new File(ClassLoader.getSystemResource("picture.jpg").toURI()), doc))
            .addText(400, 200, data -> "Hello World as text " + data.size())
            .fontSize(8)
            .addText(400, 150, data -> "Second text " + data.size())
            .addJfreeChartAndImage(100, 0,
                    chartBuilder("Average salary per age", "Salary (€)").
                            subTitle("some sub title").
                            addSeries("2016", Color.RED, from("date", "value")).
                            build(),
                    200, 100, data -> makeImage())
            .addJfreeChart(100, 250,
                    chartBuilder("Second chart", "Salary (€)").
                            subTitle("some sub title").
                            addSeries("2016", Color.BLUE, from("date", "value2")).
                            build())
            .addBufferedImage(100, 200, data -> makeImage())
            .build();


    @Test
    public void testMakeFile() throws Exception {
        //OK this isn't a great test. We really want to check we've adding the text and the images...
        //But it's a start: we check the file was created and that no exceptions were thrown.
        outputPdf.delete();
        var processor = processTemplate("/test.pdf", parts);
        processor.accept(Example.data, doc -> doc.save("output.pdf"));

        var makeBytes = processTemplateAndReturn("/test.pdf", parts, doc -> {
            try (var stream = new ByteArrayOutputStream()) {
                doc.save(stream);
                return stream.toByteArray();
            }
        });
        byte[] bytes = makeBytes.apply(Example.data);
//        System.out.println(Arrays.toString(bytes))  ;
        assertTrue(outputPdf.exists());

    }

}
