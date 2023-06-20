package org.hcl.pdftemplate.freeChart;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import one.xingyi.optics.IFold;
import one.xingyi.tuples.Tuple2;
import org.hcl.pdftemplate.FunctionWithException;

import java.awt.*;
import java.util.List;
import java.util.function.Function;


@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class DateAndValueGraphDefn<Data, XData> {
    final FunctionWithException<Data, String> title;
    final FunctionWithException<Data, String> subTitle;
    final FunctionWithException<Data, String> xAxis;
    final FunctionWithException<Data, String> yAxis;
    final boolean showXLines;
    final boolean showYLines;
    final List<SeriesDefn<Data, XData>> seriesDefns;
}


@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
class SeriesDefn<Data, XData> {
    final String seriesName;
    final Color seriesColor;
    final IFold<Data, Tuple2<XData, Double>> dataFn;
    final Float seriesStrokeWidth;
}