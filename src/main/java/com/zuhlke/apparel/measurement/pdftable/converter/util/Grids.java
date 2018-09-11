package com.zuhlke.apparel.measurement.pdftable.converter.util;

import com.zuhlke.apparel.measurement.pdftable.converter.model.Cell;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Grid;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Rect;

public final class Grids {
    private static final int PIXEL_TOLERANCE = 10;

    private Grids() {
    }

    public static Cell getLeftCell(final Grid grid, final Cell cell) {
        return Grids.getLeftCell(grid, cell, false);
    }

    public static Cell getLeftCell(final Grid grid, final Cell cell, final boolean adjacent) {
        if (cell == null) return null;

        for (int i = grid.getCells().indexOf(cell) - 1; i >= 0; i--) {
            final Cell left = grid.getCells().get(i);

            if (Grids.roughlyEquals(left.getRect().getTop(), cell.getRect().getTop())
                && left.getRect().getRight() <= cell.getRect().getLeft()
                && left.getRect().getWidth() > Grids.PIXEL_TOLERANCE
                && Grids.roughlyEquals(left.getRect().getHeight(), cell.getRect().getHeight())) {
                if (!adjacent || Grids.roughlyEquals(left.getRect().getRight(), cell.getRect().getLeft())) return left;
                return new Cell(new Rect(cell.getRect().getLeft() - cell.getRect().getWidth(), cell.getRect().getTop(), cell.getRect().getWidth(), cell.getRect().getHeight()));
            }
        }

        return null;
    }

    public static Cell getRightCell(final Grid grid, final Cell cell) {
        return Grids.getRightCell(grid, cell, false);
    }

    public static Cell getRightCell(final Grid grid, final Cell cell, final boolean adjacent) {
        if (cell == null) return null;

        for (int i = grid.getCells().indexOf(cell) + 1; i < grid.getCells().size(); i++) {
            final Cell right = grid.getCells().get(i);

            if (Grids.roughlyEquals(right.getRect().getTop(), cell.getRect().getTop())
                && right.getRect().getLeft() >= cell.getRect().getRight()
                && right.getRect().getWidth() > Grids.PIXEL_TOLERANCE
                && Grids.roughlyEquals(right.getRect().getHeight(), cell.getRect().getHeight())) {
                if (!adjacent || Grids.roughlyEquals(right.getRect().getLeft(), cell.getRect().getRight())) return right;
                return new Cell(new Rect(cell.getRect().getRight(), cell.getRect().getTop(), cell.getRect().getWidth(), cell.getRect().getHeight()));
            }
        }

        return null;
    }

    public static Cell getBottomCell(final Grid grid, final Cell cell) {
        return Grids.getBottomCell(grid, cell, false);
    }

    public static Cell getBottomCell(final Grid grid, final Cell cell, final boolean adjacent) {
        if (cell == null) return null;

        for (int i = grid.getCells().indexOf(cell) + 1; i < grid.getCells().size(); i++) {
            final Cell bottom = grid.getCells().get(i);

            if (Grids.roughlyEquals(bottom.getRect().getLeft(), cell.getRect().getLeft())
                && bottom.getRect().getTop() >= cell.getRect().getBottom()
                && Grids.roughlyEquals(bottom.getRect().getWidth(), cell.getRect().getWidth())
                && bottom.getRect().getHeight() > Grids.PIXEL_TOLERANCE) {
                if (!adjacent || Grids.roughlyEquals(bottom.getRect().getTop(), cell.getRect().getBottom())) return bottom;
                return new Cell(new Rect(cell.getRect().getLeft(), cell.getRect().getBottom(), cell.getRect().getWidth(), cell.getRect().getHeight()));
            }
        }

        return null;
    }

    public static Cell findCell(final Grid grid, final Cell templateCell) {
        if (templateCell == null) return null;

        for (final Cell cell : grid) {
            if (Rects.roughlyEquals(cell.getRect(), templateCell.getRect())) return cell;
        }

        return templateCell;
    }

    public static boolean roughlyEquals(final double a, final double b) {
        return Math.abs(a - b) < Grids.PIXEL_TOLERANCE;
    }
}
