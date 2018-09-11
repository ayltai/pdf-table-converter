package com.zuhlke.apparel.measurement.pdftable.converter.model;

import java.util.Objects;

public final class Rect {
    //region Variables

    private int x;
    private int y;
    private int width;
    private int height;

    //endregion

    //region Constructors

    public Rect() {
        this(0, 0, 0, 0);
    }

    public Rect(final int x, final int y, final int width, final int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    //endregion

    //region Properties

    public int getLeft() {
        return this.x;
    }

    public int getRight() {
        return this.x + this.width;
    }

    public int getTop() {
        return this.y;
    }

    public int getBottom() {
        return this.y + this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    //endregion

    @Override
    public String toString() {
        return "Rect { x = " + this.x + ", y = " + this.y + ", width = " + this.width + ", height = " + this.height + " }";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Rect rect = (Rect)o;
        return this.x == rect.x && this.y == rect.y && this.width == rect.width && this.height == rect.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.width, this.height);
    }
}
