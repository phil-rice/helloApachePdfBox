package org.hcl.pdftemplate.freeChart;

import lombok.var;
import org.hcl.pdftemplate.FunctionWithException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;

/**
 * A facard to capture common functionality for making charts.
 */
public interface IMakeJFreeChart {

    static IMakeJFreeChart getInstance() {return new MakeJFreeChart();}
    <Data> FunctionWithException<Data, JFreeChart> makeTimeChart(FunctionWithException<Data, DateAndValueGraphDefn<Data, RegularTimePeriod>> defnFn);
}

class MakeJFreeChart implements IMakeJFreeChart {

    @Override
    public <Data> FunctionWithException<Data, JFreeChart> makeTimeChart(FunctionWithException<Data, DateAndValueGraphDefn<Data, RegularTimePeriod>> defnFn) {
        return data -> {
            var defn = defnFn.apply(data);
            TimeSeriesCollection dataset = makeTimeDataSet(defn).apply(data);
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    defn.title,
                    defn.xAxis,
                    defn.yAxis,
                    dataset);
            XYPlot plot = chart.getXYPlot();

            var renderer = new XYLineAndShapeRenderer();
            for (int i = 0; i < dataset.getSeriesCount(); i++) {
                SeriesDefn<Data, RegularTimePeriod> seriesDefn = defn.seriesDefns.get(i);
                renderer.setSeriesPaint(i, seriesDefn.seriesColor);
                renderer.setSeriesStroke(i, new BasicStroke(seriesDefn.seriesStrokeWidth));
            }

            plot.setRenderer(renderer);
            plot.setBackgroundPaint(Color.white);

            if (defn.showYLines) {
                plot.setRangeGridlinesVisible(true);
                plot.setRangeGridlinePaint(Color.lightGray);
            }
            if (defn.showXLines) {
                plot.setDomainGridlinesVisible(true);
                plot.setDomainGridlinePaint(Color.lightGray);
            }
            chart.getLegend().setFrame(BlockBorder.NONE);

            chart.setTitle(new TextTitle(defn.title, new Font("Serif", Font.BOLD, 18)));
            if (defn.subTitle != null && defn.subTitle.length() > 0)
                chart.setSubtitles(Arrays.asList(new TextTitle(defn.subTitle, new Font("Serif", Font.PLAIN,
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