package com.zuhlke.apparel.measurement.pdftable.converter.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Grid implements Iterable<Cell> {
    public static final int DESCRIPTION_MIN_LENGTH = 4;

    private final List<Cell> cells = new ArrayList<>();

    public Grid() {
    }

    public List<Cell> getCells() {
        return this.cells;
    }

    @Override
    public Iterator<Cell> iterator() {
        return this.cells.iterator();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (final Cell cell : this.cells) {
            if (builder.length() > 0) builder.append(",\n");
            builder.append(cell.toString());
        }

        return "Grid { cells = " + builder.toString() + " }";
    }
}
