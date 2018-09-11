package com.zuhlke.apparel.measurement.pdftable.converter.model;

import java.util.Objects;

public final class Cell implements Cloneable {
    //region Variables

    private Rect     rect;
    private String   text;
    private CellType type;

    //endregion

    //region Constructors

    public Cell(final Rect rect) {
        this(rect, null);
    }

    public Cell(final Rect rect, final String text) {
        this.rect = rect;
        this.text = text;
    }

    //endregion

    //region Properties

    public Rect getRect() {
        return this.rect;
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public CellType getType() {
        return this.type;
    }

    public void setType(final CellType type) {
        this.type = type;
    }

    //endregion

    @Override
    public String toString() {
        return "Cell { rect = " + this.rect + ", text = " + this.text + ", type = " + this.type + " }";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Cell cell = (Cell)o;
        return Objects.equals(this.rect, cell.rect) && Objects.equals(this.text, cell.text) && this.type == cell.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.rect, this.text, this.type);
    }

    @Override
    public Cell clone() {
        final Cell cell = new Cell(this.rect, this.text);
        cell.setType(this.type);

        return cell;
    }
}
