package org.hcl.pdftemplate.freeChart;

import lombok.*;
import one.xingyi.optics.IFold;
import one.xingyi.tuples.Tuple2;
import org.hcl.pdftemplate.FunctionWithException;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;


@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class DateAndValueGraphDefn<Data, XData> {
    final FunctionWithException<Data, String> title;
    final FunctionWithException<Data, String> subTitle;
    final FunctionWithException<Data, ValueAxis> xAxis;
    final FunctionWithException<Data, ValueAxis> yAxis;
    final BiFunction<List<SeriesDefn<Data, XData>>, XYDataset, XYLineAndShapeRenderer> renderer;
    final boolean showXLines;
    final boolean showYLines;
    final List<SeriesDefn<Data, XData>> seriesDefns;
    final boolean legend;
}


