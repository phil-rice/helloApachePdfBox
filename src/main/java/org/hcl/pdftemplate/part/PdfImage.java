package org.hcl.pdftemplate.part;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.hcl.pdftemplate.BiFunctionWithException;
import org.hcl.pdftemplate.IPdfPrinter;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class PdfImage<Data> implements IPdfPart<Data> {
    private final float x;
    private final float y;
    private final int pageNo;
    private final BiFunctionWithException<PDDocument, Data, PDImageXObject> image;

    @Override
    public void print(IPdfPrinter printer, PDPageContentStream stream, Data data) throws Exception {
        printer.printImage(stream, data, this);
    }
}
