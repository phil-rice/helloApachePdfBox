package org.hcl.helloApache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
@Getter
@ToString
public class PdfBuilder {
    static public PdfBuilder builder() {
        return new PdfBuilder();
    }

    final List<IPdfPart> parts = new ArrayList<>();
    int pageNo = 0;
    PDType1Font font = PDType1Font.TIMES_ROMAN;
    int fontSize = 12;

    public List<IPdfPart> build() {
        return Collections.unmodifiableList(parts);
    }

    private PdfBuilder with(IPdfPart part) {
        parts.add(part);
        return this;
    }

    public PdfBuilder pageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public PdfBuilder font(PDType1Font font) {
        this.font = font;
        return this;
    }

    public PdfBuilder fontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public PdfBuilder addText(float x, float y, String text) {
        return with(new PdfText(x, y, pageNo, font, fontSize, text));
    }

    public PdfBuilder addImage(float x, float y, FunctionWithException<PDDocument, PDImageXObject> image) {
        return with(new PdfImage(x, y, pageNo, image));
    }

    public PdfBuilder addBufferedImage(float x, float y, BufferedImage image) {
        return with(new PdfBufferedImage(x, y, pageNo, image));
    }

}
