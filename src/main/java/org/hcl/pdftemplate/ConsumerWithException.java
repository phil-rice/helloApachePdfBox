package org.hcl.pdftemplate;

public interface ConsumerWithException<From> {
    void accept(From from) throws Exception;
}
