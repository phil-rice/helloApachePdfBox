package org.hcl.pdftemplate;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class PdfTextTest {

    PdfText text = new PdfText(1, 2, 3, null, 4, "text");

    IPdfPrinter mockPrinter() {
        var printer = Mockito.mock(IPdfPrinter.class);
        return printer;
    }

    @Test
    void testTextConstructor() {
        assertEquals(1, text.getX());
        assertEquals(2, text.getY());
        assertEquals(3, text.getPageNo());
        assertEquals(4, text.getFontSize());
        assertEquals("text", text.getText());
    }

    @Test
    void testTextPrint() throws Exception {
        var printer = Mockito.mock(IPdfPrinter.class);
        var stream = Mockito.mock(PDPageContentStream.class);
        text.print(printer, stream);
        Mockito.verify(printer, times(1)).printText(stream, text);
    }

    @Test
    void testImage() {
        FunctionWithException<PDDocument, PDImageXObject> makeImage = mock(FunctionWithException.class);
        var image = new PdfImage(1, 2, 3, makeImage);
        assertEquals(1, image.getX());
        assertEquals(2, image.getY());
        assertEquals(3, image.getPageNo());
        assertEquals(makeImage, image.getImage());

    }

    @Test
    void testImagePrint() throws Exception {
        var printer = Mockito.mock(IPdfPrinter.class);
        var stream = Mockito.mock(PDPageContentStream.class);
        var image = new PdfImage(1, 2, 3, null);
        image.print(printer, stream);
        Mockito.verify(printer, times(1)).printImage(stream, image);
    }

    @Test
    void testBufferedImage() {
        var image = Mockito.mock(BufferedImage.class);
        var pdfImage = new PdfBufferedImage(1, 2, 3, image);
        assertEquals(1, pdfImage.getX());
        assertEquals(2, pdfImage.getY());
        assertEquals(3, pdfImage.getPageNo());
        assertEquals(image, pdfImage.getImage());
    }

    @Test
    void testBufferedImagePrint() throws Exception {
        var printer = Mockito.mock(IPdfPrinter.class);
        var stream = Mockito.mock(PDPageContentStream.class);
        var image = new PdfBufferedImage(1, 2, 3, null);
        image.print(printer, stream);
        Mockito.verify(printer, times(1)).printBufferedImage(stream, image);
    }
}