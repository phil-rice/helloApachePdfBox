package org.hcl.pdftemplate.freeChart;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import one.xingyi.optics.IFold;
import one.xingyi.tuples.Tuple2;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.RegularTimePeriod;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


@ToString
@EqualsAndHashCode
public class Builder<Data, XData> {
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
    public static <Data> Builder<Data, RegularTimePeriod> dateBuilder(String title, String yAxis) {
        Builder<Data, RegularTimePeriod> result = new Builder<>();
        result.xDataClass = RegularTimePeriod.class;
        result.title = title;
        result.yAxis = yAxis;
        return result;
    }
    public Builder<Data, XData> subTitle(String subTitle) {this.subTitle = subTitle; return this;}
    public Builder<Data, XData> xAxis(String xAxis) {this.xAxis = xAxis; return this;}
    public Builder<Data, XData> showXLines(boolean showXLines) {this.showXLines = showXLines; return this;}
    public Builder<Data, XData> showYLines(boolean showYLines) {this.showYLines = showYLines; return this;}
    public Builder<Data, XData> seriesStrokeWidth(Float seriesStrokeWidth) {this.seriesStrokeWidth = seriesStrokeWidth; return this;}
    public Builder<Data, XData> addSeries(String seriesName, Color color, IFold<Data, Tuple2<XData, Double>> dataFn) {
        seriesDefns.add(new SeriesDefn<>(seriesName, color, dataFn, seriesStrokeWidth));
        return this;
    }
    public Function<Data, JFreeChart> build() {
        return build(IMakeJFreeChart.getInstance());
    }

    public Function<Data, DateAndValueGraphDefn<Data, XData>> buildDefn() {
        return data -> new DateAndValueGraphDefn<>(title, subTitle, xAxis, yAxis, showXLines, showYLines, seriesDefns);
    }
    public Function<Data, JFreeChart> build(IMakeJFreeChart makeJFreeChart) {
        if (xDataClass != RegularTimePeriod.class) throw new RuntimeException("Only RegularTimePeriod is supported");
        return makeJFreeChart.makeTimeChart((DateAndValueGraphDefn<Data, RegularTimePeriod>) buildDefn());
    }
}
