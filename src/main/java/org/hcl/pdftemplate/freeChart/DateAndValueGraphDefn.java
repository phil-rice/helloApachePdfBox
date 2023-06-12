package org.hcl.pdftemplate.freeChart;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import one.xingyi.optics.IFold;
import one.xingyi.tuples.Tuple2;

import java.awt.*;
import java.util.List;


@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class DateAndValueGraphDefn<Data, XData> {
    final String title;
    final String subTitle;
    final String xAxis;
    final String yAxis;
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