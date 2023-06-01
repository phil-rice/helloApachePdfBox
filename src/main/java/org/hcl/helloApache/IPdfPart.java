package org.hcl.helloApache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

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
    public void print(IPdfPrinter builder) throws Exception {
        builder.addText(this);
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
    public void print(IPdfPrinter builder) throws Exception {
        builder.addBufferedImage(this);
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
    public void print(IPdfPrinter builder) throws Exception {
        builder.addImage(this);
    }
}