package org.hcl.pdftemplate.freeChart;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import one.xingyi.tuples.Tuple2;

import java.awt.*;
import java.util.ArrayList;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ColorRange {
    public static ColorRange create(Color defaultColour) {
        return new ColorRange(defaultColour, new ArrayList<>());
    }
    public ColorRange add(double y, Color color) {
        sortedcolours.add(Tuple2.of(y, color));
        sortedcolours.sort((t1, t2) -> t1.t1.compareTo(t2.t1));
        return this;
    }
    final Color defaultColour;
    final java.util.List<Tuple2<Double, Color>> sortedcolours;
    public Color getColor(double y) {
        for (Tuple2<Double, Color> t : sortedcolours) if (y <= t.t1) return t.t2;
        return defaultColour;
    }
}
