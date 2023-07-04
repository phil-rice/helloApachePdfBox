package org.hcl.pdftemplate.freeChart;

import lombok.*;
import one.xingyi.optics.IFold;
import one.xingyi.tuples.Tuple2;
import org.hcl.pdftemplate.FunctionWithException;
import org.jfree.chart.axis.ValueAxis;

import java.awt.*;
import java.util.List;


@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class DateAndValueGraphDefn<Data, XData> {
    final FunctionWithException<Data, String> title;
    final FunctionWithException<Data, String> subTitle;
    final FunctionWithException<Data, ValueAxis> xAxis;
    final FunctionWithException<Data, ValueAxis> yAxis;
    final boolean showXLines;
    final boolean showYLines;
    final List<SeriesDefn<Data, XData>> seriesDefns;
    final boolean legend;
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