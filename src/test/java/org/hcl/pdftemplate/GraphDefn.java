package org.hcl.pdftemplate;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.var;
import org.hcl.pdftemplate.freeChart.ChartBuilder;
import org.jfree.data.time.RegularTimePeriod;

import java.awt.*;

import static org.hcl.pdftemplate.IntegrationTest.makeRectangle;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
class GraphLayout {
    public static GraphLayout defaultLayout = new GraphLayout(400, 200, 50, 50, 50);
    final int graphWidth;
    final int graphHeight;
    final int titleHeight;
    final int title1Width;
    final int title2Width;

}
@RequiredArgsConstructor
class GraphContents<Data> {
    final FunctionWithException<Data, String> graphTitleFn;
    final FunctionWithException<Data, String> textFn1;
    final FunctionWithException<Data, String> textFn2;
    final FunctionWithException<Data, String> graphYaxisFn;
    final FunctionWithException<ChartBuilder<Data, RegularTimePeriod>, ChartBuilder<Data, RegularTimePeriod>> chartFn;
    public GraphDefn<Data> displayAt(int x, int y, GraphLayout layout) {
        return new GraphDefn<>(x, y, layout, this);
    }
    public GraphDefn<Data> displayAt(int x, int y) {
        return displayAt(x, y, GraphLayout.defaultLayout);
    }
}
@RequiredArgsConstructor
public class GraphDefn<Data> implements IPdfBuilderParts<Data> {
    final int x;
    final int y;
    final GraphLayout layout;
    final GraphContents<Data> contents;
    @Override
    public PdfBuilder<Data> apply(PdfBuilder<Data> builder) throws Exception {
        var titley = y + layout.graphHeight;
        var title0Width = layout.graphWidth - (layout.title1Width + layout.title2Width);

        return builder
                .addBufferedImage(x, titley, data -> makeRectangle(title0Width, layout.titleHeight, Color.RED))
                .addBufferedImage(x + title0Width, titley, data -> makeRectangle(layout.title1Width, layout.titleHeight, Color.BLUE))
                .addBufferedImage(x + title0Width + layout.title1Width, titley,
                        data -> makeRectangle(layout.title2Width, layout.titleHeight, Color.YELLOW))
                .addText(x + 5, titley + 20, contents.graphTitleFn)
                .addText(x + title0Width + 5, titley + 20, contents.textFn1)
                .addText(x + title0Width + layout.title1Width + 5, titley + 20, contents.textFn2)
                .addJfreeChart(x, y, layout.graphWidth, layout.graphHeight, contents.chartFn.apply(ChartBuilder.forDataChart(data -> null, contents.graphYaxisFn)).build());
    }
}

