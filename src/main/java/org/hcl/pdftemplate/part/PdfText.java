package org.hcl.pdftemplate.part;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.hcl.pdftemplate.FunctionWithException;
import org.hcl.pdftemplate.IPdfPrinter;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class PdfText<Data> implements IPdfPart<Data> {
    private final float x;
    private final float y;
    private final int pageNo;
    private final PDType1Font font;
    private final int fontSize;
    private final FunctionWithException<Data,String> text;

    @Override
    public void print(IPdfPrinter printer, PDPageContentStream stream, Data data) throws Exception {
        printer.printText(stream, data, this);
    }
}
