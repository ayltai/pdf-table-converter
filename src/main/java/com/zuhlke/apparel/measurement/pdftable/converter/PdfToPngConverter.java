package com.zuhlke.apparel.measurement.pdftable.converter;

import java.io.IOException;

final class PdfToPngConverter {
    private static final int DPI = 150;

    private PdfToPngConverter() {
    }

    public static void convert(final String pdfPath, final String pngPathPrefix) throws IOException, InterruptedException {
        PdfToPngConverter.convert(pdfPath, pngPathPrefix, PdfToPngConverter.DPI);
    }

    public static void convert(final String pdfPath, final String pngPathPrefix, final int dpi) throws IOException, InterruptedException {
        if (pdfPath == null) throw new NullPointerException("Could not open PDF file for reading");

        Process process = null;

        try {
            process = new ProcessBuilder("pdftoppm", "-r", String.valueOf(dpi), "-png", pdfPath, pngPathPrefix).start();

            switch (process.waitFor()) {
                case 0:
                    break;

                case 1:
                    throw new IOException("Could not open PDF file for reading");

                case 2:
                    throw new IOException("Could not open PNG file for writing");

                case 3:
                    throw new SecurityException("Could not open PDF file for reading because it is password protected");

                default:
                    throw new IOException();
            }
        } finally {
            if (process != null) process.destroy();
        }
    }
}
