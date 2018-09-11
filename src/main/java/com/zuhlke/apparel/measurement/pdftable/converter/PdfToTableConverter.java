package com.zuhlke.apparel.measurement.pdftable.converter;

import java.io.IOException;
import java.util.List;

import com.zuhlke.apparel.measurement.pdftable.converter.model.Table;

public final class PdfToTableConverter {
    public static void main(final String[] args) throws IOException, InterruptedException {
        final long startTime = System.currentTimeMillis();
        for (final Table table : PdfToTableConverter.convert(args[0], true)) System.out.println(table.toString());
        System.out.println("Conversion time: " + String.valueOf((System.currentTimeMillis() - startTime) / 1000d));
    }

    public static List<Table> convert(final String pdfPath) throws IOException, InterruptedException {
        return PdfToTableConverter.convert(pdfPath, false);
    }

    static List<Table> convert(final String pdfPath, final boolean debug) throws IOException, InterruptedException {
        return GridToTableConverter.convert(PdfToGirdConverter.convert(pdfPath, debug));
    }
}
