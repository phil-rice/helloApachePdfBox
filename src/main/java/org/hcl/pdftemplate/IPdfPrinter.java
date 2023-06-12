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
import java.io.InputStream;
import java.util.List;

import static org.hcl.pdftemplate.freeChart.BufferedImageUtils.removeAlphaChannel;

interface IPdfPart<Data> {
    void print(IPdfPrinter printer, PDPageContentStream stream, Data data) throws Exception;

    float getX();

    float getY();

    int getPageNo();
}


public interface IPdfPrinter {
    static <Data, To> FunctionWithException<Data, To> processTemplateAndReturn(String resource, List<IPdfPart<Data>> parts, FunctionWithException<PDDocument, To> consumer) {
        return data -> {
            InputStream stream = IPdfPrinter.class.getResourceAsStream(resource);
            if (stream == null) throw new IllegalArgumentException("Resource not found: " + resource);
            try (PDDocument doc = PDDocument.load(stream)) {
                print(doc, data, parts);
                return consumer.apply(doc);
            }
        };
    }
    static <Data> BiConsumerWithException<Data, ConsumerWithException<PDDocument>> processTemplate(String resource, List<IPdfPart<Data>> parts) {
        return (data, consumer) -> {
            InputStream stream = IPdfPrinter.class.getResourceAsStream(resource);
            if (stream == null) throw new IllegalArgumentException("Resource not found: " + resource);
            try (PDDocument doc = PDDocument.load(stream)) {
                print(doc, data, parts);
                consumer.accept(doc);
            }
        };
    }

    static <Data> void print(PDDocument doc, Data data, List<IPdfPart<Data>> parts) throws Exception {
        IPdfPrinter printer = new PdfPrinter(doc);
        for (IPdfPart part : parts)
            withStream(doc, part.getPageNo(), stream ->//there is an obvious optimization still to do here: group the items by page and only make one stream per page
                    part.print(printer, stream, data));
    }

    static void withStream(PDDocument doc, int pageNo, ConsumerWithException<PDPageContentStream> consumer) throws Exception {
        PDPage page = doc.getDocumentCatalog().getPages().get(pageNo);
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false, true)) {
            consumer.accept(contentStream);
        }
    }

    <Data> void printText(PDPageContentStream stream, Data data, PdfText<Data> text) throws Exception;

    <Data> void printImage(PDPageContentStream stream, Data data, PdfImage<Data> image) throws Exception;

    <Data> void printBufferedImage(PDPageContentStream stream, Data data, PdfBufferedImage<Data> image) throws Exception;

    <Data> void printJFreeChart(PDPageContentStream stream, Data data, PdfJFreeChart<Data> pdfJFreeChart) throws Exception;
}

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
class PdfPrinter implements IPdfPrinter {

    private final PDDocument doc;


    @Override
    public <Data> void printText(PDPageContentStream stream, Data data, PdfText<Data> text) throws Exception {
        stream.beginText();
        try {
            stream.setFont(text.getFont(), text.getFontSize());
            stream.newLineAtOffset(text.getX(), text.getY());
            stream.showText(text.getText().apply(data));
        } finally {
            stream.endText();
        }
    }

    @Override
    public <Data> void printBufferedImage(PDPageContentStream stream, Data data, PdfBufferedImage<Data> image) throws Exception {
        BufferedImage bufferedImage = image.getImage().apply(data);
        PDImageXObject jpg = JPEGFactory.createFromImage(doc, bufferedImage);
        stream.drawImage(jpg, image.getX(), image.getY());
    }

    @Override
    public <Data> void printImage(PDPageContentStream stream, Data data, PdfImage<Data> image) throws Exception {
        stream.drawImage(image.getImage().apply(doc, data), image.getX(), image.getY());
    }


    @Override
    public <Data> void printJFreeChart(PDPageContentStream stream, Data data, PdfJFreeChart<Data> pdfJFreeChart) throws Exception {
        BufferedImage rawImage = pdfJFreeChart.getChart().apply(data).createBufferedImage(pdfJFreeChart.getWidth() * 3, pdfJFreeChart.getHeight() * 3);
        BufferedImage image = removeAlphaChannel(rawImage, Color.WHITE.getRGB());
        PDImageXObject jpg = JPEGFactory.createFromImage(doc, image);
        stream.drawImage(jpg, pdfJFreeChart.getX(), pdfJFreeChart.getY(), pdfJFreeChart.getWidth(), pdfJFreeChart.getHeight());
    }
}


