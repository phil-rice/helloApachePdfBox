package org.hcl.pdftemplate.freeChart;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import one.xingyi.optics.IFold;
import one.xingyi.tuples.Tuple2;
import org.hcl.pdftemplate.FunctionWithException;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.RegularTimePeriod;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


@ToString
@EqualsAndHashCode
public class ChartBuilder<Data, XData> {
    Class<XData> xDataClass;
    String title = "";
    String subTitle = "";
    String xAxis = "Date";
    String yAxis = "Value";
    boolean showXLines = false;
    boolean showYLines = false;

    //For the series
    Float seriesStrokeWidth = 2.0f;

    List<SeriesDefn<Data, XData>> seriesDefns = new ArrayList<>();
    public static <Data> ChartBuilder<Data, RegularTimePeriod> forDataChart(String title, String yAxis) {
        ChartBuilder<Data, RegularTimePeriod> result = new ChartBuilder<>();
        result.xDataClass = RegularTimePeriod.class;
        result.title = title;
        result.yAxis = yAxis;
        return result;
    }
    public ChartBuilder<Data, XData> subTitle(String subTitle) {this.subTitle = subTitle; return this;}
    public ChartBuilder<Data, XData> xAxis(String xAxis) {this.xAxis = xAxis; return this;}
    public ChartBuilder<Data, XData> showXLines(boolean showXLines) {this.showXLines = showXLines; return this;}
    public ChartBuilder<Data, XData> showYLines(boolean showYLines) {this.showYLines = showYLines; return this;}
    public ChartBuilder<Data, XData> seriesStrokeWidth(Float seriesStrokeWidth) {this.seriesStrokeWidth = seriesStrokeWidth; return this;}
    public ChartBuilder<Data, XData> addSeries(String seriesName, Color color, IFold<Data, Tuple2<XData, Double>> dataFn) {
        seriesDefns.add(new SeriesDefn<>(seriesName, color, dataFn, seriesStrokeWidth));
        return this;
    }
    public FunctionWithException<Data, JFreeChart> build() {
        return build(IMakeJFreeChart.getInstance());
    }

    public FunctionWithException<Data, DateAndValueGraphDefn<Data, XData>> buildDefn() {
        return data -> new DateAndValueGraphDefn<>(title, subTitle, xAxis, yAxis, showXLines, showYLines, seriesDefns);
    }
    public FunctionWithException<Data, JFreeChart> build(IMakeJFreeChart makeJFreeChart) {
        if (xDataClass != RegularTimePeriod.class) throw new RuntimeException("Only RegularTimePeriod is supported");
        FunctionWithException<Data, DateAndValueGraphDefn<Data, XData>> result = buildDefn();
        FunctionWithException<Data, DateAndValueGraphDefn<Data, RegularTimePeriod>> cast = (FunctionWithException) buildDefn();
        return makeJFreeChart.makeTimeChart(cast);
    }
}
