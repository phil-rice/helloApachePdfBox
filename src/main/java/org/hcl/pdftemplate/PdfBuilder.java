package org.hcl.pdftemplate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.hcl.pdftemplate.part.*;
import org.jfree.chart.JFreeChart;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

@EqualsAndHashCode
@Getter
@ToString
@RequiredArgsConstructor
public class PdfBuilder<Data> {

    final ResourceBundle bundle;
    public String getStringFromBundle(String key) {return bundle.getString(key);}
    static public <Data> PdfBuilder<Data> builder(ResourceBundle bundle) {
        return new PdfBuilder<Data>(bundle);
    }


    final List<IPdfPart<Data>> parts = new ArrayList<>();
    int pageNo = 0;
    PDType1Font font = PDType1Font.TIMES_ROMAN;
    int fontSize = 12;

    public List<IPdfPart<Data>> build() {
        return Collections.unmodifiableList(parts);
    }

    private PdfBuilder<Data> with(IPdfPart<Data> part) {
        parts.add(part);
        return this;
    }

    public PdfBuilder<Data> pageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public PdfBuilder<Data> font(PDType1Font font) {
        this.font = font;
        return this;
    }

    public PdfBuilder<Data> fontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public PdfBuilder<Data> addText(float x, float y, FunctionWithException<Data, String> text) {
        return with(new PdfText<>(x, y, pageNo, font, fontSize, text));
    }
    public PdfBuilder<Data> addI18n(float x, float y, String resourceKey, FunctionWithException<Data, Object[]> params) {
        return with(new PdfText<>(x, y, pageNo, font, fontSize, data -> {
            String format = bundle.getString(resourceKey);
            return MessageFormat.format(format, params.apply(data));
        }));
    }
    public PdfBuilder<Data> addI18n(float x, float y, String resourceKey) {
        return with(new PdfText<>(x, y, pageNo, font, fontSize, data -> bundle.getString(resourceKey)));
    }

    public PdfBuilder<Data> addParts(FunctionWithException<PdfBuilder<Data>, PdfBuilder<Data>> fn) throws Exception {
        return fn.apply(this);
    }

    public PdfBuilder<Data> addJfreeChart(float x, float y, FunctionWithException<Data, JFreeChart> chart) {
        return this.addJfreeChart(x, y, 300, 200, chart);
    }
    public PdfBuilder<Data> addJfreeChartAndImage(float x, float y, FunctionWithException<Data, JFreeChart> chart, float xOffset, float yOffset, FunctionWithException<Data, BufferedImage> image) {
        return this.addJfreeChart(x, y, 300, 200, chart).addBufferedImage(x + xOffset, y + yOffset, image);
    }

    public PdfBuilder<Data> addJfreeChart(float x, float y, int width, int height, FunctionWithException<Data, JFreeChart> chart) {
        return with(new PdfJFreeChart<>(x, y, width, height, pageNo, chart));
    }

    public PdfBuilder<Data> addImage(float x, float y, BiFunctionWithException<PDDocument, Data, PDImageXObject> image) {
        return with(new PdfImage<>(x, y, pageNo, image));
    }

    public PdfBuilder<Data> addBufferedImage(float x, float y, FunctionWithException<Data, BufferedImage> image) {
        return with(new PdfBufferedImage<>(x, y, pageNo, image));
    }

}
