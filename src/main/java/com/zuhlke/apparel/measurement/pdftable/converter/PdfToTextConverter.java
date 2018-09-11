package com.zuhlke.apparel.measurement.pdftable.converter;

import java.io.IOException;

import com.zuhlke.apparel.measurement.pdftable.converter.model.Rect;

final class PdfToTextConverter {
    private static final int DPI = 150;

    private PdfToTextConverter() {
    }

    public static void convert(final String pdfPath, final String textPath, final int page, final Rect rect) throws IOException, InterruptedException {
        PdfToTextConverter.convert(pdfPath, textPath, page, rect, PdfToTextConverter.DPI);
    }

    public static void convert(final String pdfPath, final String textPath, final int page, final Rect rect, final int dpi) throws IOException, InterruptedException {
        if (pdfPath == null) throw new NullPointerException("Could not open PDF file for reading");
        if (textPath == null) throw new NullPointerException("Could not open text file for writing");

        Process process = null;

        try {
            process = new ProcessBuilder("pdftotext", "-r", String.valueOf(dpi), "-f", String.valueOf(page), "-l", String.valueOf(page), "-x", String.valueOf(rect.getLeft()), "-y", String.valueOf(rect.getTop()), "-W", String.valueOf(rect.getWidth()), "-H", String.valueOf(rect.getHeight()), "-layout", pdfPath, textPath).start();

            switch (process.waitFor()) {
                case 0:
                    break;

                case 1:
                    throw new IOException("Could not open PDF file for reading");

                case 2:
                    throw new IOException("Could not open text file for writing");

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
