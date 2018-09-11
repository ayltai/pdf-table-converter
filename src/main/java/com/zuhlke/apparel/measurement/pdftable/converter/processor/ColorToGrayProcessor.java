package com.zuhlke.apparel.measurement.pdftable.converter.processor;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public final class ColorToGrayProcessor implements Processor {
    public static final class Builder {
        private Builder() {
        }

        public ColorToGrayProcessor build() {
            return new ColorToGrayProcessor();
        }
    }

    public static ColorToGrayProcessor.Builder builder() {
        return new ColorToGrayProcessor.Builder();
    }

    @Override
    public Mat apply(final Mat... inputFrames) {
        if (inputFrames == null) throw new NullPointerException("Input frames are null");
        if (inputFrames.length != 1) throw new IllegalArgumentException("One and only one input frame is required");
        if (inputFrames[0].channels() <= 1) throw new IllegalArgumentException("Only multi-channel input frames are supported");

        final Mat outputFrame = new Mat(inputFrames[0].rows(), inputFrames[0].cols(), CvType.CV_8UC1);
        Imgproc.cvtColor(inputFrames[0], outputFrame, Imgproc.COLOR_BGR2GRAY);

        return outputFrame;
    }
}
