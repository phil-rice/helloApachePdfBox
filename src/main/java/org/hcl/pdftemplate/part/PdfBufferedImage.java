package org.hcl.pdftemplate.part;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.hcl.pdftemplate.FunctionWithException;
import org.hcl.pdftemplate.IPdfPrinter;

import java.awt.image.BufferedImage;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class PdfBufferedImage<Data> implements IPdfPart<Data> {
    private final float x;
    private final float y;
    private final int pageNo;
    private final FunctionWithException<Data, BufferedImage> image;

    @Override
    public void print(IPdfPrinter printer, PDPageContentStream stream, Data data) throws Exception {
        printer.printBufferedImage(stream, data, this);
    }
}
