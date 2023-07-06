package org.hcl.pdftemplate;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.var;
import one.xingyi.optics.IFold;
import one.xingyi.tuples.Tuple2;
import one.xingyi.utils.StreamHelper;
import org.hcl.pdftemplate.freeChart.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.hcl.pdftemplate.Example.dateFromString;
import static org.hcl.pdftemplate.IntegrationTest.makeRectangle;
import static org.hcl.pdftemplate.utils.BundleUtils.toStringFn;

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
    static IFold<List<Map<String, Object>>, Tuple2<RegularTimePeriod, Double>> from(String dateKey, String valueKey) {
        return IFold.of(list -> list.stream().map(map -> Tuple2.of(dateFromString(map.get(dateKey)), (Double) map.get(valueKey))));
    }
    static GraphContents<List<Map<String, Object>>> fromListOfMap(String bundlePrefix, String value) {
        return new GraphContents<>(
                GraphContents.from("date", value),
                bundlePrefix + ".title",
                bundlePrefix + ".text1",
                bundlePrefix + ".text2",
                bundlePrefix + ".xAxis",
                bundlePrefix + ".yAxis",
                bundlePrefix + ".color");


    }

    final IFold<Data, Tuple2<RegularTimePeriod, Double>> fold;
    final String titleKey;
    final String textKey1;
    final String textKey2;
    final String graphXAxisKey;
    final String graphYaxisKey;
    final String seriesColorKey;

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
        FunctionWithException<Data, Double> last = data -> StreamHelper.last(contents.fold.all(data)).t2;
        FunctionWithException<Data, Double> avgOfLast12 = data -> {
            List<Tuple2<RegularTimePeriod, Double>> lastFew = StreamHelper.lastN(contents.fold.all(data), 12).collect(Collectors.toList());
            return lastFew.stream().collect(Collectors.averagingDouble(d -> d.t2));
        };

        return builder
                .fontSize(10)
                .addBufferedImage(x, titley, data -> makeRectangle(title0Width, layout.titleHeight, Color.RED))
                .addBufferedImage(x + title0Width, titley, data -> makeRectangle(layout.title1Width, layout.titleHeight, Color.BLUE))
                .addBufferedImage(x + title0Width + layout.title1Width, titley,
                        data -> makeRectangle(layout.title2Width, layout.titleHeight, Color.YELLOW))
                .addI18n(x + 5, titley + 20, contents.titleKey)
                .addI18n(x + title0Width + 5, titley + 20, contents.textKey1)
                .addText(x + title0Width + 5, titley + 10, toStringFn("%5.2f", avgOfLast12))
                .addI18n(x + title0Width + layout.title1Width + 5, titley + 20, contents.textKey2)
                .addText(x + title0Width + layout.title1Width + 5, titley + 10, toStringFn("%5.2f", last))
                .addJfreeChart(x, y, layout.graphWidth, layout.graphHeight,
                        ChartBuilder.<Data>forDataChart(data -> null)
                                .yAxis(IMakeJFreeChart.valueAxis(ignore -> builder.bundle.getString(contents.graphYaxisKey)))
                                .addSeries(builder.getFromBundle(contents.graphXAxisKey),
                                        builder.getColorFromBundle(contents.seriesColorKey),
                                        contents.fold).
                                build());
    }
}

