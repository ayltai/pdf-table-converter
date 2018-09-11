package com.zuhlke.apparel.measurement.pdftable.converter.processor;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public final class VerticalLineDetector implements Processor {
    private static final double SCALE     = 16;
    private static final int    ITERATION = 1;

    public static final class Builder {
        private double scale     = VerticalLineDetector.SCALE;
        private int    iteration = VerticalLineDetector.ITERATION;

        private Builder() {
        }

        public VerticalLineDetector.Builder scale(final double scale) {
            this.scale = scale;
            return this;
        }

        public VerticalLineDetector.Builder iteration(final int iteration) {
            this.iteration = iteration;
            return this;
        }

        public VerticalLineDetector build() {
            return new VerticalLineDetector(this.scale, this.iteration);
        }
    }

    private final double scale;
    private final int    iteration;

    public static VerticalLineDetector.Builder builder() {
        return new VerticalLineDetector.Builder();
    }

    private VerticalLineDetector(final double scale, final int iteration) {
        this.scale     = scale;
        this.iteration = iteration;
    }

    @Override
    public Mat apply(final Mat... inputFrames) {
        if (inputFrames == null) throw new NullPointerException("Input frames are null");
        if (inputFrames.length != 1) throw new IllegalArgumentException("One and only one input frame is required");
        if (inputFrames[0].channels() != 1) throw new IllegalArgumentException("Only single-channel input frame is supported");

        final Mat outputFrame = inputFrames[0].clone();
        final Mat structure   = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, outputFrame.rows() / this.scale));

        Imgproc.erode(outputFrame, outputFrame, structure);
        Imgproc.dilate(outputFrame, outputFrame, structure, new Point(-1, -1), this.iteration);

        return outputFrame;
    }
}
