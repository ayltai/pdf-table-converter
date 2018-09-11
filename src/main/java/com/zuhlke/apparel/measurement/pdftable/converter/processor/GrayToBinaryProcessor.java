package com.zuhlke.apparel.measurement.pdftable.converter.processor;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public final class GrayToBinaryProcessor implements Processor {
    private static final int MAX_VALUE   = 255;
    private static final int BLOCK_SIZE  = 15;
    private static final int MEAN_OFFSET = -2;

    public static final class Builder {
        private int maxValue   = GrayToBinaryProcessor.MAX_VALUE;
        private int blockSize  = GrayToBinaryProcessor.BLOCK_SIZE;
        private int meanOffset = GrayToBinaryProcessor.MEAN_OFFSET;

        private Builder() {
        }

        public GrayToBinaryProcessor.Builder maxValue(final int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public GrayToBinaryProcessor.Builder blockSize(final int blockSize) {
            this.blockSize = blockSize;
            return this;
        }

        public GrayToBinaryProcessor.Builder meanOffset(final int meanOffset) {
            this.meanOffset = meanOffset;
            return this;
        }

        public GrayToBinaryProcessor build() {
            return new GrayToBinaryProcessor(this.maxValue, this.blockSize, this.meanOffset);
        }
    }

    private final int maxValue;
    private final int blockSize;
    private final int meanOffset;

    public static GrayToBinaryProcessor.Builder builder() {
        return new GrayToBinaryProcessor.Builder();
    }

    private GrayToBinaryProcessor(final int maxValue, final int blockSize, final int meanOffset) {
        this.maxValue   = maxValue;
        this.blockSize  = blockSize;
        this.meanOffset = meanOffset;
    }

    @Override
    public Mat apply(final Mat... inputFrames) {
        if (inputFrames == null) throw new NullPointerException("Input frames are null");
        if (inputFrames.length != 1) throw new IllegalArgumentException("One and only one input frame is required");
        if (inputFrames[0].channels() != 1) throw new IllegalArgumentException("Only single-channel input frame is supported");

        final Mat outputFrame = new Mat(inputFrames[0].rows(), inputFrames[0].cols(), CvType.CV_8UC1);

        Core.bitwise_not(inputFrames[0], inputFrames[0]);
        Imgproc.adaptiveThreshold(inputFrames[0], outputFrame, this.maxValue, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, this.blockSize, this.meanOffset);

        return outputFrame;
    }
}
