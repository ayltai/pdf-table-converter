package com.zuhlke.apparel.measurement.pdftable.converter.util;

import java.util.List;
import java.util.regex.Pattern;

import com.zuhlke.apparel.measurement.pdftable.converter.model.Cell;
import com.zuhlke.apparel.measurement.pdftable.converter.model.CellType;

public final class Cells {
    private Cells() {
    }

    public static Cell findPositiveToleranceCell(final List<Cell> headers) {
        for (final Cell header : headers) {
            if (CellType.TOLERANCE == header.getType() || CellType.TOLERANCE_POSITIVE == header.getType()) return header;
        }

        return null;
    }

    public static Cell findNegativeToleranceCell(final List<Cell> headers) {
        for (final Cell header : headers) {
            if (CellType.TOLERANCE == header.getType() || CellType.TOLERANCE_NEGATIVE == header.getType()) return header;
        }

        return null;
    }

    public static Cell findMatchingHeader(final List<Cell> headers, final Cell cell) {
        if (cell == null) return null;

        for (final Cell header : headers) {
            if (Grids.roughlyEquals(header.getRect().getLeft(), cell.getRect().getLeft())
                && Grids.roughlyEquals(header.getRect().getWidth(), cell.getRect().getWidth())
                && header.getRect().getBottom() <= cell.getRect().getTop()) return header;
        }

        return null;
    }

    public static Cell cloneAndUpdate(final Cell cell, final String text) {
        final Cell clone = cell.clone();
        clone.setText(text);

        return clone;
    }

    public static boolean isNumber(final Cell cell) {
        if (cell == null) return false;
        if (Strings.isNullOrEmpty(cell.getText())) return false;

        try {
            Integer.parseInt(cell.getText().replaceAll(",", "").replaceAll(Pattern.quote("."), ""));
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }
}
