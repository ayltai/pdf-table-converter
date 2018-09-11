package com.zuhlke.apparel.measurement.pdftable.converter.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Table implements Iterable<Row> {
    private final List<Row> rows = new ArrayList<>();

    public Table() {
    }

    public List<Row> getRows() {
        return this.rows;
    }

    public boolean isEmpty() {
        return this.rows.isEmpty();
    }

    @Override
    public Iterator<Row> iterator() {
        return this.rows.iterator();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (final Row row : this.rows) {
            if (builder.length() > 0) builder.append(",\n");
            builder.append(row.toString());
        }

        return "Table { rows = " + builder.toString() + " }";
    }
}
