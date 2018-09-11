package com.zuhlke.apparel.measurement.pdftable.converter.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public final class Points {
    private Points() {
    }

    public static List<Point> toPoints(final Mat frame) {
        if (frame == null) return Collections.emptyList();

        final List<Point> points = new ArrayList<>();
        for (int i = 0; i < frame.rows(); i++) {
            for (int j = 0; j < frame.cols(); j++) {
                if (frame.get(i, j)[0] > 0) points.add(new Point(j, i));
            }
        }

        return points;
    }

    public static void sort(final List<Point> points) {
        if (points == null || points.isEmpty()) return;

        points.sort((p1, p2) -> {
            // FIXME: This kind of comparison is non-transitive and violates Comparator's contract
            if (Grids.roughlyEquals(p1.y, p2.y)) {
                final int result = Double.compare(p1.x, p2.x);
                if (result == 0) return Double.compare(p1.y, p2.y);
                return result;
            }

            final int result = Double.compare(p1.y, p2.y);
            if (result == 0) return Double.compare(p1.x, p2.x);
            return result;
        });
    }
}
