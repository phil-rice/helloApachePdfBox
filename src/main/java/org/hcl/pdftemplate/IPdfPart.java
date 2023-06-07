package org.hcl.pdftemplate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.JFreeChart;

import java.awt.image.BufferedImage;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
class PdfText implements IPdfPart {
    private final float x;
    private final float y;
    private final int pageNo;
    private final PDType1Font font;
    private final int fontSize;
    private final String text;

    @Override
    public void print(IPdfPrinter printer, PDPageContentStream stream) throws Exception {
        printer.printText(stream, this);
    }
}

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
class PdfBufferedImage implements IPdfPart {
    private final float x;
    private final float y;
    private final int pageNo;
    private final BufferedImage image;

    @Override
    public void print(IPdfPrinter printer, PDPageContentStream stream) throws Exception {
        printer.printBufferedImage(stream, this);
    }
}

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
class PdfImage implements IPdfPart {
    private final float x;
    private final float y;
    private final int pageNo;
    private final FunctionWithException<PDDocument, PDImageXObject> image;

    @Override
    public void print(IPdfPrinter printer, PDPageContentStream stream) throws Exception {
        printer.printImage(stream, this);
    }
}

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
class PdfJFreeChart implements IPdfPart {
    private final float x;
    private final float y;
    private final int width; //these are just for resolution issues
    private final int height;
    private final int pageNo;
    private final JFreeChart chart;

    @Override
    public void print(IPdfPrinter printer, PDPageContentStream stream) throws Exception {
        printer.printJFreeChart(stream, this);
    }
}