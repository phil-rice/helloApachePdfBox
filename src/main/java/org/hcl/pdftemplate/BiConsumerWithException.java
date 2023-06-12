package org.hcl.pdftemplate;

public interface BiConsumerWithException<From1,From2> {
    void accept(From1 from1, From2 from2) throws Exception;
}
