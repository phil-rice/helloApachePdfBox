package org.hcl.pdftemplate.freeChart;

import lombok.var;
import org.hcl.pdftemplate.FunctionWithException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;

/**
 * A facard to capture common functionality for making charts.
 */
public interface IMakeJFreeChart {

    static IMakeJFreeChart getInstance() {return new MakeJFreeChart();}
    <Data> FunctionWithException<Data, JFreeChart> makeTimeChart(FunctionWithException<Data, DateAndValueGraphDefn<Data, RegularTimePeriod>> defnFn);

    static <Data> FunctionWithException<Data, ValueAxis> timeAxis(Function<Data, String> labelFn) {
        return data -> {
            ValueAxis timeAxis = new DateAxis(labelFn.apply(data));
            timeAxis.setLowerMargin(0.02);  // reduce the default margins
            timeAxis.setUpperMargin(0.02);
            return timeAxis;
        };
    }
    static <Data> FunctionWithException<Data, ValueAxis> valueAxis(Function<Data, String> valueAxisLabel) {
        return data -> {
            NumberAxis valueAxis = new NumberAxis(valueAxisLabel.apply(data));
            valueAxis.setAutoRangeIncludesZero(false);  // override default
            return valueAxis;
        };
    }

}

class MakeJFreeChart implements IMakeJFreeChart {
    private static ChartTheme currentTheme = new StandardChartTheme("JFree");
    public static JFreeChart createTimeSeriesChart(String title,
                                                   ValueAxis xAxis, ValueAxis yAxis, XYDataset dataset,
                                                   boolean legend) {

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;

    }


    @Override
    public <Data> FunctionWithException<Data, JFreeChart> makeTimeChart(FunctionWithException<Data, DateAndValueGraphDefn<Data, RegularTimePeriod>> defnFn) {
        return data -> {
            var defn = defnFn.apply(data);
            TimeSeriesCollection dataset = makeTimeDataSet(defn).apply(data);
            String title = defn.title.apply(data);
            JFreeChart chart = createTimeSeriesChart(
                    title,
                    defn.xAxis.apply(data),
                    defn.yAxis.apply(data),
                    dataset,
                    defn.legend);
            XYPlot plot = chart.getXYPlot();
            var renderer = defn.renderer.apply(defn.seriesDefns, dataset);
            if (renderer != null) plot.setRenderer(renderer);
            plot.setBackgroundPaint(Color.white);

            if (defn.showYLines) {
                plot.setRangeGridlinesVisible(true);
                plot.setRangeGridlinePaint(Color.lightGray);
            }
            if (defn.showXLines) {
                plot.setDomainGridlinesVisible(true);
                plot.setDomainGridlinePaint(Color.lightGray);
            }
            if (defn.legend) chart.getLegend().setFrame(BlockBorder.NONE);

//            chart.setTitle(new TextTitle(title, new Font("Serif", Font.BOLD, 18)));
            String subTitle = defn.subTitle.apply(data);
            if (subTitle != null && subTitle.length() > 0)
                chart.setSubtitles(Arrays.asList(new TextTitle(subTitle, new Font("Serif", Font.PLAIN,
                        12))));

            return chart;
        };
    }

    <Data> Function<Data, TimeSeriesCollection> makeTimeDataSet(DateAndValueGraphDefn<Data, RegularTimePeriod> defn) {
        return data -> {
            TimeSeriesCollection result = new TimeSeriesCollection();
            defn.seriesDefns.stream().map(seriesDefn -> {
                var series = new TimeSeries(seriesDefn.seriesName);
                seriesDefn.dataFn.all(data).forEach(t -> series.add(t.t1, t.t2));
                return series;
            }).forEach(result::addSeries);
            return result;
        };
    }
}