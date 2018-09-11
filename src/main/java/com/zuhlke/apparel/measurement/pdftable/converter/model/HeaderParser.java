package com.zuhlke.apparel.measurement.pdftable.converter.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.zuhlke.apparel.measurement.pdftable.converter.util.Cells;
import com.zuhlke.apparel.measurement.pdftable.converter.util.Strings;

public final class HeaderParser {
    //region Constants

    static final List<String> NUMBERED_SIZES = Arrays.asList("26", "28", "30", "32", "34", "36", "38", "40", "42", "44", "46", "48", "50", "52", "54", "56", "58", "60");
    static final List<String> LABELED_SIZES  = Arrays.asList("XXXS", "XXS", "XS", "S", "M", "L", "XL", "1X", "1XL", "XXL", "2X", "2XL", "XXXL", "3X", "3XL", "XXXXL", "4X", "4XL", "3ANS", "4ANS", "5ANS", "6ANS", "8ANS", "10ANS", "12ANS", "14ANS", "16ANS");

    private static final List<String> TOLERANCES          = Arrays.asList("TOL +/-", "TOL. +/-", "TOL -/+", "TOL. -/+", "TOL+/-", "TOL.+/-", "TOL-/+", "TOL.-/+", "TOL (+/-)", "TOL. (+/-)", "TOL (-/+)", "TOL. (-/+)", "TOL(+/-)", "TOL.(+/-)", "TOL(-/+)", "TOL.(-/+)", "TOLERANCES (+/-)", "TOLERANCES (-/+)", "TOLERANCES(+/-)", "TOLERANCES(-/+)", "TOLERANCES +/-", "TOLERANCES -/+", "TOLERANCES+/-", "TOLERANCES-/+");
    private static final List<String> POSITIVE_TOLERANCES = Arrays.asList("TOL +", "TOL. +", "TOL+", "TOL.+", "TOL (+)", "TOL. (+)", "TOL(+)", "TOL.(+)", "TOLERANCE +", "TOLERANCE+", "TOLERANCE (+)", "TOLERANCE(+)");
    private static final List<String> NEGATIVE_TOLERANCES = Arrays.asList("TOL -", "TOL. -", "TOL-", "TOL.-", "TOL (-)", "TOL. (-)", "TOL(-)", "TOL.(-)", "TOLERANCE -", "TOLERANCE-", "TOLERANCE (-)", "TOLERANCE(-)");

    //endregion

    private final List<Cell> cells = new ArrayList<>();

    public HeaderParser() {
    }

    public List<Cell> getHeaderCells() {
        return this.cells;
    }

    //region Methods

    public boolean parse(final Cell cell) {
        if (cell == null || Strings.isNullOrEmpty(cell.getText())) return false;
        if (this.cells.isEmpty()) return this.parseFirst(cell);

        if (this.parseSizeNumbered(cell) || this.parseSizeLabeled(cell) || this.parseTolerance(cell) || this.parseTolerancePositive(cell) || this.parseToleranceNegative(cell)) {
            this.cells.add(Cells.cloneAndUpdate(cell, HeaderParser.clean(cell.getText())));
            return true;
        }

        return false;
    }

    private boolean parseFirst(final Cell cell) {
        final String text = HeaderParser.clean(cell.getText());

        if (HeaderParser.NUMBERED_SIZES.contains(text)) {
            cell.setType(CellType.SIZE_NUMBERED);
        } else if (HeaderParser.LABELED_SIZES.contains(text)) {
            cell.setType(CellType.SIZE_LABELED);
        } else if (HeaderParser.TOLERANCES.contains(text)) {
            cell.setType(CellType.TOLERANCE);
        } else if (HeaderParser.POSITIVE_TOLERANCES.contains(text)) {
            cell.setType(CellType.TOLERANCE_POSITIVE);
        } else if (HeaderParser.NEGATIVE_TOLERANCES.contains(text)) {
            cell.setType(CellType.TOLERANCE_NEGATIVE);
        } else {
            return false;
        }

        cell.setText(text);

        this.cells.add(cell);

        return true;
    }

    private boolean parseSizeNumbered(final Cell cell) {
        if (this.cells.isEmpty()) return false;

        final Cell     previousCell = this.cells.get(this.cells.size() - 1);
        final CellType previousType = previousCell.getType();
        final String   previousText = HeaderParser.clean(previousCell.getText());
        final String   text         = HeaderParser.clean(cell.getText());

        if (HeaderParser.NUMBERED_SIZES.contains(text)) {
            if (CellType.SIZE_NUMBERED == previousType) {
                if (HeaderParser.NUMBERED_SIZES.indexOf(text) > HeaderParser.NUMBERED_SIZES.indexOf(previousText)) {
                    cell.setType(CellType.SIZE_NUMBERED);
                    return true;
                }
            } else if (CellType.TOLERANCE == previousType
                || CellType.TOLERANCE_POSITIVE == previousType
                || CellType.TOLERANCE_NEGATIVE == previousType
                || CellType.UNKNOWN == previousType) {
                cell.setType(CellType.SIZE_NUMBERED);
                return true;
            }
        }

        return false;
    }

    private boolean parseSizeLabeled(final Cell cell) {
        if (this.cells.isEmpty()) return false;

        final Cell     previousCell = this.cells.get(this.cells.size() - 1);
        final CellType previousType = previousCell.getType();
        final String   previousText = HeaderParser.clean(previousCell.getText());
        final String   text         = HeaderParser.clean(cell.getText());

        if (HeaderParser.LABELED_SIZES.contains(text)) {
            if (CellType.SIZE_LABELED == previousType) {
                if (HeaderParser.LABELED_SIZES.indexOf(text) > HeaderParser.NUMBERED_SIZES.indexOf(previousText)) {
                    cell.setType(CellType.SIZE_LABELED);
                    return true;
                }
            } else if (CellType.TOLERANCE == previousType
                || CellType.TOLERANCE_POSITIVE == previousType
                || CellType.TOLERANCE_NEGATIVE == previousType
                || CellType.UNKNOWN == previousType) {
                cell.setType(CellType.SIZE_LABELED);
                return true;
            }
        }

        return false;
    }

    private boolean parseTolerance(final Cell cell) {
        if (this.cells.isEmpty()) return false;

        final Cell     previousCell = this.cells.get(this.cells.size() - 1);
        final CellType previousType = previousCell.getType();
        final String   text         = HeaderParser.clean(cell.getText());

        if (HeaderParser.TOLERANCES.contains(text)) {
            if (CellType.SIZE_NUMBERED == previousType
                || CellType.SIZE_LABELED == previousType
                || CellType.UNKNOWN == previousType) {
                cell.setType(CellType.TOLERANCE);
                return true;
            }
        }

        return false;
    }

    private boolean parseTolerancePositive(final Cell cell) {
        if (this.cells.isEmpty()) return false;

        final Cell     previousCell = this.cells.get(this.cells.size() - 1);
        final CellType previousType = previousCell.getType();
        final String   text         = HeaderParser.clean(cell.getText());

        if (HeaderParser.POSITIVE_TOLERANCES.contains(text)) {
            if (CellType.SIZE_NUMBERED == previousType
                || CellType.SIZE_LABELED == previousType
                || CellType.TOLERANCE_NEGATIVE == previousType
                || CellType.UNKNOWN == previousType) {
                cell.setType(CellType.TOLERANCE_POSITIVE);
                return true;
            }
        }

        return false;
    }

    private boolean parseToleranceNegative(final Cell cell) {
        if (this.cells.isEmpty()) return false;

        final Cell     previousCell = this.cells.get(this.cells.size() - 1);
        final CellType previousType = previousCell.getType();
        final String   text         = HeaderParser.clean(cell.getText());

        if (HeaderParser.NEGATIVE_TOLERANCES.contains(text)) {
            if (CellType.SIZE_NUMBERED == previousType
                || CellType.SIZE_LABELED == previousType
                || CellType.TOLERANCE_POSITIVE == previousType
                || CellType.UNKNOWN == previousType) {
                cell.setType(CellType.TOLERANCE_NEGATIVE);
                return true;
            }
        }

        return false;
    }

    //endregion Methods

    private static String clean(final String text) {
        return text.replaceAll("\n", "")
            .replaceAll("\r", "")
            .replaceAll(" ", "")
            .replaceAll("\u200B", "")
            .toUpperCase();
    }
}
