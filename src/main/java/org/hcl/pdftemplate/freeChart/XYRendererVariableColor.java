package org.hcl.pdftemplate.freeChart;

import lombok.RequiredArgsConstructor;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.util.function.BiFunction;


@RequiredArgsConstructor
public class XYRendererVariableColor extends XYLineAndShapeRenderer {
    public static <Data, XData> BiFunction<java.util.List<SeriesDefn<Data, XData>>, XYDataset, XYLineAndShapeRenderer> create(ColorRange colorRange) {
        return (series, dataSet) -> new XYRendererVariableColor(colorRange, dataSet);
    }
    final ColorRange colorRange;
    final XYDataset dataset;

    @Override
    public Paint getItemPaint(int row, int col) {
        Paint cpaint = getItemColor(row, col);
        if (cpaint == null) cpaint = super.getItemPaint(row, col);
        return cpaint;
    }

    public Color getItemColor(int row, int col) {
        double y = dataset.getYValue(row, col);
        return colorRange.getColor(y);
    }

    @Override
    protected void drawFirstPassShape(Graphics2D g2, int pass, int series,
                                      int item, Shape shape) {
        g2.setStroke(getItemStroke(series, item));
        Color c1 = getItemColor(series, item);
        Color c2 = getItemColor(series, item - 1);
        GradientPaint linePaint = new GradientPaint(0, 0, c1, 0, 300, c2);
        g2.setPaint(linePaint);
        g2.draw(shape);
    }
};

