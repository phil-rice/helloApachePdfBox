package org.hcl.pdftemplate;

import lombok.var;
import org.hcl.pdftemplate.freeChart.ChartBuilder;
import org.hcl.pdftemplate.part.IPdfPart;
import org.jfree.data.time.RegularTimePeriod;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static org.hcl.pdftemplate.GraphContents.fromListOfMap;
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

    private static ChartBuilder<List<Map<String, Object>>, RegularTimePeriod> chartBuilder(FunctionWithException<List<Map<String, Object>>, String> title) {
        return ChartBuilder.forDataChart(title);
    }

    GraphDefn<List<Map<String, Object>>> defn1 =
            new GraphDefn<>(0, 0, GraphLayout.defaultLayout,
                    fromListOfMap("one", "value"));

    public static GraphLayout layout = new GraphLayout(250, 150, 30, 50, 50);

    ResourceBundle bundle = ResourceBundle.getBundle("graphconfig");
    List<IPdfPart<List<Map<String, Object>>>> parts = PdfBuilder.<List<Map<String, Object>>>builder(bundle)
            .addParts(new GraphDefn<List<Map<String, Object>>>(0, 0, layout,
                    GraphContents.fromListOfMap("one", "value")))

            .addParts(new GraphDefn<List<Map<String, Object>>>(280, 0, layout,
                    GraphContents.fromListOfMap("one", "value2")))

            .addParts(new GraphDefn<List<Map<String, Object>>>(0, 400, layout,
                    GraphContents.fromListOfMap("one", "value")))
            .addParts(new GraphDefn<List<Map<String, Object>>>(280, 400, layout,
                    GraphContents.fromListOfMap("one", "value2")))
            .build();


    @Test
    public void testMakeFile() throws Exception {
        //OK this isn't a great test. We really want to check we've adding the text and the images...
        //But it's a start: we check the file was created and that no exceptions were thrown.
        outputPdf.delete();
        var processor = processTemplate("/test.pdf", parts);
        processor.accept(Example.data, doc -> doc.save("output.pdf"));

        FunctionWithException<List<Map<String, Object>>, byte[]> makeBytes = processTemplateAndReturn("/test.pdf", parts, doc -> {
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
