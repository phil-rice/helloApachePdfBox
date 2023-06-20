package org.hcl.pdftemplate.part;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.hcl.pdftemplate.IPdfPrinter;
public interface IPdfPart<Data> {
    void print(IPdfPrinter printer, PDPageContentStream stream, Data data) throws Exception;

    float getX();

    float getY();

    int getPageNo();
}

