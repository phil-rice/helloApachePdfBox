package org.hcl.helloApache;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.hcl.helloApache.IPdfPrinter.updateTemplate;

public class Hello {

    static BufferedImage makeImage() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, 100, 100);
        return image;
    }

    public static void main(String[] args) throws Exception {
        var parts = PdfBuilder.builder()
                .addText(400, 200, "Hello World as text")
                .addText(400, 300, "Second text")
                .addBufferedImage(100, 200, makeImage())
                .addImage(100, 400, doc -> PDImageXObject.createFromFileByContent(new File(ClassLoader.getSystemResource("picture.jpg").toURI()), doc))
                .build();

        updateTemplate("test.pdf", parts, doc -> doc.save("HelloWorld.pdf"));
    }
}
