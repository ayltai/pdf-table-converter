package com.zuhlke.apparel.measurement.pdftable.converter.processor;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public final class HorizontalLineDetector implements Processor {
    private static final double SCALE     = 16;
    private static final int    ITERATION = 1;

    public static final class Builder {
        private double scale     = HorizontalLineDetector.SCALE;
        private int    iteration = HorizontalLineDetector.ITERATION;

        private Builder() {
        }

        public HorizontalLineDetector.Builder scale(final double scale) {
            this.scale = scale;
            return this;
        }

        public HorizontalLineDetector.Builder iteration(final int iteration) {
            this.iteration = iteration;
            return this;
        }

        public HorizontalLineDetector build() {
            return new HorizontalLineDetector(this.scale, this.iteration);
        }
    }

    private final double scale;
    private final int    iteration;

    public static HorizontalLineDetector.Builder builder() {
        return new HorizontalLineDetector.Builder();
    }

    private HorizontalLineDetector(final double scale, final int iteration) {
        this.scale     = scale;
        this.iteration = iteration;
    }

    @Override
    public Mat apply(final Mat... inputFrames) {
        if (inputFrames == null) throw new NullPointerException("Input frames are null");
        if (inputFrames.length != 1) throw new IllegalArgumentException("One and only one input frame is required");
        if (inputFrames[0].channels() != 1) throw new IllegalArgumentException("Only single-channel input frame is supported");

        final Mat outputFrame = inputFrames[0].clone();
        final Mat structure   = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(outputFrame.cols() / this.scale, 1));

        Imgproc.erode(outputFrame, outputFrame, structure);
        Imgproc.dilate(outputFrame, outputFrame, structure, new Point(-1, -1), this.iteration);

        return outputFrame;
    }
}
