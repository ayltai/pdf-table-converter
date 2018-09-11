package com.zuhlke.apparel.measurement.pdftable.converter;

import java.util.ArrayList;
import java.util.List;

import com.zuhlke.apparel.measurement.pdftable.converter.model.Cell;
import com.zuhlke.apparel.measurement.pdftable.converter.model.CellType;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Grid;
import com.zuhlke.apparel.measurement.pdftable.converter.model.HeaderParser;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Rect;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Row;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Table;
import com.zuhlke.apparel.measurement.pdftable.converter.util.Cells;
import com.zuhlke.apparel.measurement.pdftable.converter.util.Grids;
import com.zuhlke.apparel.measurement.pdftable.converter.util.Strings;

final class GridToTableConverter {
    private GridToTableConverter() {
    }

    public static List<Table> convert(final List<Grid> grids) {
        final List<Table> tables = new ArrayList<>(grids.size());
        List<Cell> headers = new ArrayList<>();

        for (final Grid grid : grids) {
            final Table table = new Table();

            final List<Cell> headerCells = GridToTableConverter.findHeaders(grid);
            if (!headerCells.isEmpty()) headers = headerCells;

            if (!headers.isEmpty()) {
                int  i           = 0;
                Cell bottomCell  = headers.get(i++);
                Row  previousRow = null;
                Row  currentRow;

                while (true) {
                    if (previousRow == null) {
                        while (i < headers.size()) {
                            bottomCell = Grids.getBottomCell(grid, bottomCell);
                            if (bottomCell == null) {
                                bottomCell = headers.get(i++);
                            } else {
                                break;
                            }
                        }
                        if (bottomCell == null) break;

                        currentRow = GridToTableConverter.fillSizeCells(grid, headers, bottomCell);
                        if (currentRow == null) break;

                        GridToTableConverter.fillCodeAndDescriptionCells(grid, bottomCell, currentRow);
                    } else {
                        currentRow = GridToTableConverter.findNextRow(grid, previousRow, headers);
                        if (currentRow == null) break;

                        GridToTableConverter.fillSizeCells(grid, headers, currentRow, previousRow);
                        if (currentRow.isEmpty()) break;
                    }

                    previousRow = currentRow;

                    if (!currentRow.isEmpty()) table.getRows().add(currentRow);
                }

                if (!table.isEmpty()) tables.add(table);
            }
        }

        return tables;
    }

    private static List<Cell> findHeaders(final Grid grid) {
        final HeaderParser parser = new HeaderParser();

        Cell    cell  = grid.getCells().get(0);
        boolean found = false;

        while (true) {
            if (parser.parse(cell)) found = true;

            final Cell rightCell = Grids.getRightCell(grid, cell);
            if (!found && rightCell == null) {
                final int next = grid.getCells().indexOf(cell) + 1;
                if (next == grid.getCells().size()) break;

                cell = grid.getCells().get(next);
            } else {
                if (rightCell == null) break;

                cell = rightCell;
            }
        }

        return parser.getHeaderCells();
    }

    private static Row fillSizeCells(final Grid grid, final List<Cell> headers, final Cell bottom) {
        final Row row = new Row();

        boolean found = false;

        Cell rightCell = Grids.getLeftCell(grid, bottom);
        for (Cell header : headers) {
            while (true) {
                rightCell = Grids.getRightCell(grid, rightCell);

                if (rightCell == null) {
                    if (found) break;

                    return null;
                } else {
                    found = true;
                }

                header = Cells.findMatchingHeader(headers, rightCell);
                if (header == null) break;

                if (Grids.roughlyEquals(header.getRect().getLeft(), rightCell.getRect().getLeft())
                    && Grids.roughlyEquals(header.getRect().getWidth(), rightCell.getRect().getWidth())) break;
            }

            if (header == null) continue;

            if (CellType.SIZE_NUMBERED == header.getType() || CellType.SIZE_LABELED == header.getType()) {
                row.setSize(header.getText(), rightCell);
            } else if (CellType.TOLERANCE == header.getType()) {
                row.setPositiveTolerance(rightCell);

                if (rightCell == null) {
                    row.setNegativeTolerance(null);
                } else {
                    final Cell negative = rightCell.clone();
                    negative.setText("-" + negative.getText());
                    row.setNegativeTolerance(negative);
                }
            } else if (CellType.TOLERANCE_POSITIVE == header.getType()) {
                row.setPositiveTolerance(rightCell);
            } else if (CellType.TOLERANCE_NEGATIVE == header.getType()) {
                row.setNegativeTolerance(rightCell);
            }
        }

        return row;
    }

    private static void fillSizeCells(final Grid grid, final List<Cell> headers, final Row currentRow, final Row previousRow) {
        for (final Cell header : headers) {
            if (CellType.SIZE_LABELED == header.getType() || CellType.SIZE_NUMBERED == header.getType()) {
                Cell bottomCell = Grids.getBottomCell(grid, previousRow.getSize(header.getText()), true);
                if (bottomCell == null || Strings.isNullOrEmpty(bottomCell.getText())) bottomCell = Grids.findCell(grid, bottomCell);
                if (bottomCell != null) currentRow.setSize(header.getText(), bottomCell);
            }
        }
    }

    private static void fillCodeAndDescriptionCells(final Grid grid, final Cell bottom, final Row row) {
        final Cell firstLeftCell = Grids.getLeftCell(grid, bottom);
        if (firstLeftCell != null) {
            if (firstLeftCell.getText().length() > Grid.DESCRIPTION_MIN_LENGTH) {
                row.setDescription(firstLeftCell);
            } else {
                row.setCode(firstLeftCell);
            }

            final Cell secondLeftCell = Grids.getLeftCell(grid, firstLeftCell);
            if (secondLeftCell != null) {
                if (secondLeftCell.getText().length() > Grid.DESCRIPTION_MIN_LENGTH) {
                    row.setDescription(secondLeftCell);
                } else {
                    row.setCode(secondLeftCell);
                }
            }
        }
    }

    private static Row findNextRow(final Grid grid, final Row previousRow, final List<Cell> headers) {
        final Cell descriptionCell = Grids.getBottomCell(grid, previousRow.getDescription());
        if (descriptionCell == null) return null;

        final Row row = new Row();
        row.setDescription(descriptionCell);

        final Cell codeCell = Grids.getBottomCell(grid, previousRow.getCode());
        if (codeCell == null) {
            final Cell rightCell = Grids.getRightCell(grid, descriptionCell);
            if (rightCell == null) {
                final Cell leftCell = Grids.getLeftCell(grid, descriptionCell);
                if (leftCell != null && !Strings.isNullOrEmpty(leftCell.getText()) && leftCell.getText().length() <= Grid.DESCRIPTION_MIN_LENGTH) row.setCode(leftCell);
            } else {
                if (!Strings.isNullOrEmpty(rightCell.getText()) && rightCell.getText().length() <= Grid.DESCRIPTION_MIN_LENGTH && !Cells.isNumber(rightCell)) row.setCode(rightCell);
            }
        } else {
            row.setCode(codeCell);
        }

        final Cell positiveToleranceCell = Grids.getBottomCell(grid, previousRow.getPositiveTolerance());
        if (positiveToleranceCell == null) {
            final Cell header = Cells.findPositiveToleranceCell(headers);
            if (header != null) {
                final Cell cell = Grids.findCell(grid, new Cell(new Rect(header.getRect().getLeft(), descriptionCell.getRect().getTop(), header.getRect().getWidth(), descriptionCell.getRect().getHeight())));
                if (cell != null) {
                    final Cell clone = !Strings.isNullOrEmpty(cell.getText())
                        ? Cells.cloneAndUpdate(cell, cell.getText().replaceAll("-", ""))
                        : cell.clone();

                    row.setPositiveTolerance(clone);
                }
            }
        } else {
            if (!Strings.isNullOrEmpty(positiveToleranceCell.getText())) {
                final Cell clone = positiveToleranceCell.getText().startsWith("-")
                    ? Cells.cloneAndUpdate(positiveToleranceCell, positiveToleranceCell.getText().replaceAll("-", ""))
                    : positiveToleranceCell.clone();

                row.setPositiveTolerance(clone);
            }
        }

        final Cell negativeToleranceCell = Grids.getBottomCell(grid, previousRow.getNegativeTolerance());
        if (negativeToleranceCell == null) {
            final Cell header = Cells.findNegativeToleranceCell(headers);
            if (header != null) {
                final Cell cell = Grids.findCell(grid, new Cell(new Rect(header.getRect().getLeft(), descriptionCell.getRect().getTop(), header.getRect().getWidth(), descriptionCell.getRect().getHeight())));
                if (cell != null) {
                    final Cell clone = !Strings.isNullOrEmpty(cell.getText())
                        ? Cells.cloneAndUpdate(cell, "-" + cell.getText())
                        : cell.clone();

                    row.setNegativeTolerance(clone);
                }
            }
        } else {
            if (!Strings.isNullOrEmpty(negativeToleranceCell.getText())) {
                final Cell clone = !negativeToleranceCell.getText().startsWith("-")
                    ? Cells.cloneAndUpdate(negativeToleranceCell, "-" + negativeToleranceCell.getText())
                    : negativeToleranceCell.clone();

                row.setNegativeTolerance(clone);
            }
        }

        return row;
    }
}
