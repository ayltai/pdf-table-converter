package com.zuhlke.apparel.measurement.pdftable.converter;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

final class PngToMatrixConverter {
    private PngToMatrixConverter() {
    }

    public static Mat convert(final String pngPath) {
        return Imgcodecs.imread(pngPath);
    }
}
