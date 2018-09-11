package com.zuhlke.apparel.measurement.pdftable.converter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuhlke.apparel.measurement.pdftable.converter.util.Strings;

public final class Row {
    //region Variables

    private final Map<String, Cell> sizes = new HashMap<>();

    private Cell code;
    private Cell description;
    private Cell positiveTolerance;
    private Cell negativeTolerance;

    //endregion

    public Row() {
    }

    //region Properties

    public List<String> getSizeNames() {
        final List<String> names = new ArrayList<>(this.sizes.keySet());
        if (names.isEmpty()) return names;

        final boolean isSizeNumbered = HeaderParser.NUMBERED_SIZES.indexOf(names.get(0)) > -1;

        names.sort((name1, name2) -> isSizeNumbered
            ? HeaderParser.NUMBERED_SIZES.indexOf(name1) - HeaderParser.NUMBERED_SIZES.indexOf(name2)
            : HeaderParser.LABELED_SIZES.indexOf(name1) - HeaderParser.LABELED_SIZES.indexOf(name2));

        return names;
    }

    public void setSize(final String size, final Cell cell) {
        this.sizes.put(size, cell);
    }

    public Cell getSize(final String size) {
        return this.sizes.get(size);
    }

    public Cell getCode() {
        return this.code;
    }

    public void setCode(final Cell code) {
        this.code = code;
    }

    public Cell getDescription() {
        return this.description;
    }

    public void setDescription(final Cell description) {
        this.description = description;
    }

    public Cell getPositiveTolerance() {
        return this.positiveTolerance;
    }

    public void setPositiveTolerance(final Cell positiveTolerance) {
        this.positiveTolerance = positiveTolerance;
    }

    public Cell getNegativeTolerance() {
        return this.negativeTolerance;
    }

    public void setNegativeTolerance(final Cell negativeTolerance) {
        this.negativeTolerance = negativeTolerance;
    }

    public boolean isEmpty() {
        if (!this.sizes.isEmpty()) return false;
        if ((this.positiveTolerance == null || Strings.isNullOrEmpty(this.positiveTolerance.getText()))
            && (this.negativeTolerance == null || Strings.isNullOrEmpty(this.negativeTolerance.getText()      ))) return true;
        return this.description == null || Strings.isNullOrEmpty(this.description.getText());
    }

    //endregion

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, Cell> entry : this.sizes.entrySet()) {
            if (builder.length() > 0) builder.append(", ");

            builder.append(entry.getKey())
                .append(":")
                .append(entry.getValue());
        }

        return "Row { code = " + this.code + ", description = " + this.description + ", positiveTolerance = " + this.positiveTolerance + ", negativeTolerance = " + this.negativeTolerance + ", sizes = " + builder.toString() + " }";
    }
}
