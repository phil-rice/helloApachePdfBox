package org.hcl.pdftemplate;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
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
        var g = image.createGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, 100, 100);
        return image;
    }

    List<IPdfPart> parts = PdfBuilder.builder()
            .addText(400, 200, "Hello World as text")
            .addText(400, 300, "Second text")
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
