package org.hcl.helloApache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

interface IPdfPart {
    void print(IPdfPrinter builder) throws Exception;

    float getX();

    float getY();

    int getPageNo();
}


public interface IPdfPrinter {

    static void updateTemplate(String resource, List<IPdfPart> parts, ConsumerWithException<PDDocument> consumer) throws Exception{
        try (PDDocument doc = PDDocument.load(IPdfPrinter.class.getResourceAsStream(resource))) {
            print(doc, parts);
            consumer.accept(doc);
        }
    }
    static void print(PDDocument doc, List<IPdfPart> parts) throws Exception {
        IPdfPrinter printer = new PdfPrinter(doc);
        for (IPdfPart part : parts) part.print(printer);

    }

    void addText(PdfText text) throws Exception;

    void addImage(PdfImage image) throws Exception;

    void addBufferedImage(PdfBufferedImage image) throws Exception;
}

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
class PdfPrinter implements IPdfPrinter {

    private final PDDocument doc;


    void withStream(IPdfPart part, ConsumerWithException<PDPageContentStream> consumer) throws Exception {
        PDPage page = doc.getDocumentCatalog().getPages().get(part.getPageNo());
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true)) {
            consumer.accept(contentStream);
        }
    }

    @Override
    public void addText(PdfText text) throws Exception {
        withStream(text, stream -> {
            stream.beginText();
            try {
                stream.setFont(text.getFont(), text.getFontSize());
                stream.newLineAtOffset(text.getX(), text.getY());
                stream.showText(text.getText());
            } finally {
                stream.endText();
            }
        });
    }

    @Override
    public void addBufferedImage(PdfBufferedImage image) throws Exception {
        withStream(image, stream -> {
            BufferedImage bufferedImage = image.getImage();
            var jpg = JPEGFactory.createFromImage(doc, bufferedImage);
            stream.drawImage(jpg, image.getX(), image.getY());
        });
    }

    @Override
    public void addImage(PdfImage image) throws Exception {
        withStream(image, stream -> {
            stream.drawImage(image.getImage().apply(doc), image.getX(), image.getY());
        });
    }
}


