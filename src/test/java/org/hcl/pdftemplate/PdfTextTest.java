package org.hcl.pdftemplate;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.hcl.pdftemplate.part.PdfBufferedImage;
import org.hcl.pdftemplate.part.PdfImage;
import org.hcl.pdftemplate.part.PdfText;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class PdfTextTest {

    PdfText<String> text = new PdfText<>(1, 2, 3, null, 4, s -> s + "_test");

    IPdfPrinter mockPrinter() {
        IPdfPrinter printer = Mockito.mock(IPdfPrinter.class);
        return printer;
    }

    @Test
    void testTextConstructor() throws Exception {
        assertEquals(1, text.getX());
        assertEquals(2, text.getY());
        assertEquals(3, text.getPageNo());
        assertEquals(4, text.getFontSize());
        assertEquals("someString_test", text.getText().apply("someString"));
    }

    @Test
    void testTextPrint() throws Exception {
        IPdfPrinter printer = Mockito.mock(IPdfPrinter.class);
        PDPageContentStream stream = Mockito.mock(PDPageContentStream.class);
        text.print(printer, stream, "someData");
        Mockito.verify(printer, times(1)).printText(stream, "someData", text);
    }

    @Test
    void testImage() {
        BiFunctionWithException<PDDocument, String, PDImageXObject> makeImage = mock(BiFunctionWithException.class);
        PdfImage<String> image = new PdfImage<>(1, 2, 3, makeImage);
        assertEquals(1, image.getX());
        assertEquals(2, image.getY());
        assertEquals(3, image.getPageNo());
        assertEquals(makeImage, image.getImage());

    }

    @Test
    void testImagePrint() throws Exception {
        IPdfPrinter printer = Mockito.mock(IPdfPrinter.class);
        PDPageContentStream stream = Mockito.mock(PDPageContentStream.class);
        PdfImage<String> image = new PdfImage<>(1, 2, 3, null);
        image.print(printer, stream, "someData");
        Mockito.verify(printer, times(1)).printImage(stream, "someData", image);
    }

    @Test
    void testBufferedImage() {
        FunctionWithException<String,BufferedImage> image = Mockito.mock(FunctionWithException.class);
        PdfBufferedImage<String> pdfImage = new PdfBufferedImage<>(1, 2, 3, image);
        assertEquals(1, pdfImage.getX());
        assertEquals(2, pdfImage.getY());
        assertEquals(3, pdfImage.getPageNo());
        assertEquals(image, pdfImage.getImage());
    }

    @Test
    void testBufferedImagePrint() throws Exception {
        IPdfPrinter printer = Mockito.mock(IPdfPrinter.class);
        PDPageContentStream stream = Mockito.mock(PDPageContentStream.class);
        PdfBufferedImage<String> image = new PdfBufferedImage<>(1, 2, 3, null);
        image.print(printer, stream, "someData");
        Mockito.verify(printer, times(1)).printBufferedImage(stream, "someData", image);
    }
}