package org.hcl.helloApache;

public interface FunctionWithException<From,To> {
    To apply(From from) throws Exception;
}
interface ConsumerWithException<From> {
    void accept(From from) throws Exception;
}