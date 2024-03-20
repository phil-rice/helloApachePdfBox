package org.hcl.pdftemplate.freeChart;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.hcl.pdftemplate.FunctionWithException;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.data.xy.XYDataset;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;


@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class DateAndValueGraphDefn<Data, XData> {
    final FunctionWithException<Data, String> title;
    final FunctionWithException<Data, String> subTitle;
    final FunctionWithException<Data, ValueAxis> xAxis;
    final FunctionWithException<Data, ValueAxis> yAxis;
    final BiFunction<List<SeriesDefn<Data, XData>>, XYDataset, AbstractXYItemRenderer> renderer;
    final Consumer<XYPlot> modifyPlot;
    final boolean showXLines;
    final boolean showYLines;
    final List<SeriesDefn<Data, XData>> seriesDefns;
    final boolean legend;
}


