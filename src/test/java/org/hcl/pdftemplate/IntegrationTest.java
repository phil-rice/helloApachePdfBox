package org.hcl.pdftemplate;

import lombok.var;
import org.hcl.pdftemplate.freeChart.ChartBuilder;
import org.hcl.pdftemplate.part.IPdfPart;
import org.jfree.data.time.RegularTimePeriod;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static org.hcl.pdftemplate.Example.from;
import static org.hcl.pdftemplate.IPdfPrinter.processTemplate;
import static org.hcl.pdftemplate.IPdfPrinter.processTemplateAndReturn;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest {

    File outputPdf = new File("output.pdf");
    public IntegrationTest() throws Exception {}

    static BufferedImage makeRectangle(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        return image;
    }

    private static ChartBuilder<List<Map<String, Object>>, RegularTimePeriod> chartBuilder(FunctionWithException<List<Map<String, Object>>, String> title, FunctionWithException<List<Map<String, Object>>, String> yAxis) {
        return ChartBuilder.forDataChart(title, yAxis);
    }

    GraphDefn<List<Map<String, Object>>> defn1 =
            new GraphDefn<>(0, 0, GraphLayout.defaultLayout,
                    new GraphContents<>(data1 -> "graph.title1", data1 -> "graph.text1", data1 -> "graph.text2",
                            data1 -> "someYAxis",
                            cb -> cb.addSeries("2016", Color.RED, from("date", "value"))));

    public static GraphLayout layout = new GraphLayout(250, 150, 30, 50, 50);
    List<IPdfPart<List<Map<String, Object>>>> parts = PdfBuilder.<List<Map<String, Object>>>builder()
            .addParts(new GraphDefn<List<Map<String, Object>>>(0, 0, layout,
                    new GraphContents<>(data -> "The title 1",
                            data -> "text1",
                            data -> "text2",
                            data -> "someYAxis",
                            cb -> cb.addSeries("2016", Color.RED, from("date", "value")))))

            .addParts(new GraphDefn<List<Map<String, Object>>>(280, 0, layout,
                    new GraphContents<>(data -> "The title 2",
                            data -> "text1",
                            data -> "text2",
                            data -> "someYAxis",
                            cb -> cb.addSeries("2016", Color.RED, from("date", "value1")))))

            .addParts(new GraphDefn<List<Map<String, Object>>>(0, 400, layout,
                    new GraphContents<>(data -> "The title 3",
                            data -> "text1",
                            data -> "text2",
                            data -> "someYAxis",
                            cb -> cb.addSeries("2016", Color.RED, from("date", "value2")))))
            .addParts(new GraphDefn<List<Map<String, Object>>>(280, 400, layout,
                    new GraphContents<>(data -> "The title 4",
                            data -> "text1",
                            data -> "text2",
                            data -> "someYAxis",
                            cb -> cb.addSeries("2016", Color.RED, from("date", "value2")))))
            .build();


    @Test
    public void testMakeFile() throws Exception {
        //OK this isn't a great test. We really want to check we've adding the text and the images...
        //But it's a start: we check the file was created and that no exceptions were thrown.
        outputPdf.delete();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");
        var processor = processTemplate("/test.pdf", resourceBundle, parts);
        processor.accept(Example.data, doc -> doc.save("output.pdf"));

        FunctionWithException<List<Map<String, Object>>, byte[]> makeBytes = processTemplateAndReturn("/test.pdf", resourceBundle, parts, doc -> {
            try (var stream = new ByteArrayOutputStream()) {
                doc.save(stream);
                return stream.toByteArray();
            }
        });

        byte[] bytes = makeBytes.apply(Example.data);
        System.out.println(Arrays.toString(bytes));
        assertTrue(outputPdf.exists());

    }

}
