package org.hcl.pdftemplate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

interface IPdfPart {
    void print(IPdfPrinter printer, PDPageContentStream stream) throws Exception;

    float getX();

    float getY();

    int getPageNo();
}


public interface IPdfPrinter {
    static void updateTemplate(String resource, List<IPdfPart> parts, ConsumerWithException<PDDocument> consumer) throws Exception {
        InputStream stream = IPdfPrinter.class.getResourceAsStream(resource);
        if (stream == null) throw new IllegalArgumentException("Resource not found: " + resource);
        try (PDDocument doc = PDDocument.load(stream)) {
            print(doc, parts);
            consumer.accept(doc);
        }
    }

    static void print(PDDocument doc, List<IPdfPart> parts) throws Exception {
        IPdfPrinter printer = new PdfPrinter(doc);
        for (IPdfPart part : parts)
            withStream(doc, part.getPageNo(), stream ->//there is an obvious optimization still to do here: group the items by page and only make one stream per page
                    part.print(printer, stream));
    }

    static void withStream(PDDocument doc, int pageNo, ConsumerWithException<PDPageContentStream> consumer) throws Exception {
        PDPage page = doc.getDocumentCatalog().getPages().get(pageNo);
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false, true)) {
            consumer.accept(contentStream);
        }
    }

    void printText(PDPageContentStream stream, PdfText text) throws Exception;

    void printImage(PDPageContentStream stream, PdfImage image) throws Exception;

    void printBufferedImage(PDPageContentStream stream, PdfBufferedImage image) throws Exception;

    void printJFreeChart(PDPageContentStream stream, PdfJFreeChart pdfJFreeChart) throws IOException;
}

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
class PdfPrinter implements IPdfPrinter {

    private final PDDocument doc;


    @Override
    public void printText(PDPageContentStream stream, PdfText text) throws Exception {
        stream.beginText();
        try {
            stream.setFont(text.getFont(), text.getFontSize());
            stream.newLineAtOffset(text.getX(), text.getY());
            stream.showText(text.getText());
        } finally {
            stream.endText();
        }
    }

    @Override
    public void printBufferedImage(PDPageContentStream stream, PdfBufferedImage image) throws Exception {
        BufferedImage bufferedImage = image.getImage();
        PDImageXObject jpg = JPEGFactory.createFromImage(doc, bufferedImage);
        stream.drawImage(jpg, image.getX(), image.getY());
    }

    @Override
    public void printImage(PDPageContentStream stream, PdfImage image) throws Exception {
        stream.drawImage(image.getImage().apply(doc), image.getX(), image.getY());
    }

    public static BufferedImage removeAlphaChannel(BufferedImage img,
                                                   int color) {
        if (!img.getColorModel().hasAlpha()) {
            return img;
        }

        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = target.createGraphics();
        g.setColor(new Color(color, false));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return target;
    }

    @Override
    public void printJFreeChart(PDPageContentStream stream, PdfJFreeChart pdfJFreeChart) throws IOException {
        BufferedImage rawImage = pdfJFreeChart.getChart().createBufferedImage(pdfJFreeChart.getWidth() * 3, pdfJFreeChart.getHeight() * 3);
        BufferedImage image = removeAlphaChannel(rawImage, Color.WHITE.getRGB());
        PDImageXObject jpg = JPEGFactory.createFromImage(doc, image);
        stream.drawImage(jpg, pdfJFreeChart.getX(), pdfJFreeChart.getY(), pdfJFreeChart.getWidth(), pdfJFreeChart.getHeight());
    }
}


