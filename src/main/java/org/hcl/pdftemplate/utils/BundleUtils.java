package org.hcl.pdftemplate.utils;

import org.hcl.pdftemplate.FunctionWithException;

import java.util.ResourceBundle;
import java.util.function.Function;

public interface BundleUtils {
    static <From, To> FunctionWithException<From, Object[]> toParams(FunctionWithException<From, To> fn) {
        return from -> new Object[]{fn.apply(from)};
    }
    static <From, To> FunctionWithException<From, String> toStringFn(String format, FunctionWithException<From, To> fn) {
        return from -> String.format(format, fn.apply(from));
    }

}
