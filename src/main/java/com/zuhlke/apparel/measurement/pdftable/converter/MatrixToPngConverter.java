package com.zuhlke.apparel.measurement.pdftable.converter;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

final class MatrixToPngConverter {
    private MatrixToPngConverter() {
    }

    public static void convert(final Mat frame, final String pngPath) {
        Imgcodecs.imwrite(pngPath, frame);
    }
}
