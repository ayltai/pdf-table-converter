package com.zuhlke.apparel.measurement.pdftable.converter;

import java.io.File;
import java.io.IOException;

public final class PdfToHtmlConverter {
    public static void main(final String[] args) throws IOException, InterruptedException {
        final long startTime = System.currentTimeMillis();
        PdfToHtmlConverter.convert(args[0], true);
        System.out.println("Conversion time: " + String.valueOf((System.currentTimeMillis() - startTime) / 1000d));
    }

    public static void convert(final String pdfPath) throws IOException, InterruptedException {
        PdfToHtmlConverter.convert(pdfPath, false);
    }

    static void convert(final String pdfPath, final boolean debug) throws IOException, InterruptedException {
        final File   pdfFile = new File(pdfPath);
        final String pdfName = pdfFile.getName();

        TableToHtmlConverter.convert(PdfToTableConverter.convert(pdfPath, debug), new File(pdfFile.getParentFile(), pdfName.substring(0, pdfName.length() - 4)).getAbsolutePath());
    }
}
