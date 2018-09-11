package com.zuhlke.apparel.measurement.pdftable.converter.util;

import com.zuhlke.apparel.measurement.pdftable.converter.model.Rect;

public final class Rects {
    private Rects() {
    }

    public static boolean roughlyEquals(final Rect rect1, final Rect rect2) {
        return Grids.roughlyEquals(rect1.getLeft(), rect2.getLeft())
            && Grids.roughlyEquals(rect1.getTop(), rect2.getTop())
            && Grids.roughlyEquals(rect1.getWidth(), rect2.getWidth())
            && Grids.roughlyEquals(rect1.getHeight(), rect2.getHeight());
    }
}
