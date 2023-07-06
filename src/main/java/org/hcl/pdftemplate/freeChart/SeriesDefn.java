package org.hcl.pdftemplate.freeChart;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import one.xingyi.optics.IFold;
import one.xingyi.tuples.Tuple2;

import java.awt.*;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class SeriesDefn<Data, XData> {
    final String seriesName;
    final Color seriesColor;
    final IFold<Data, Tuple2<XData, Double>> dataFn;
    final Float seriesStrokeWidth;
}
