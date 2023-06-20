package org.hcl.pdftemplate.part;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.hcl.pdftemplate.FunctionWithException;
import org.hcl.pdftemplate.IPdfPrinter;
import org.jfree.chart.JFreeChart;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class PdfJFreeChart<Data> implements IPdfPart<Data> {
    private final float x;
    private final float y;
    private final int width; //these are just for resolution issues
    private final int height;
    private final int pageNo;
    private final FunctionWithException<Data, JFreeChart> chart;

    @Override
    public void print(IPdfPrinter printer, PDPageContentStream stream, Data data) throws Exception {
        printer.printJFreeChart(stream, data, this);
    }
}
