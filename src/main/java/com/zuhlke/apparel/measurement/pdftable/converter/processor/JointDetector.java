package com.zuhlke.apparel.measurement.pdftable.converter.processor;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public final class JointDetector implements Processor {
    public static final class Builder {
        private Builder() {
        }

        public JointDetector build() {
            return new JointDetector();
        }
    }

    public static JointDetector.Builder builder() {
        return new JointDetector.Builder();
    }

    private JointDetector() {
    }

    @Override
    public Mat apply(final Mat... inputFrames) {
        if (inputFrames == null) throw new NullPointerException("Input frames are null");
        if (inputFrames.length != 2) throw new IllegalArgumentException("Two and only two input frames are required");

        final Mat outputFrame = inputFrames[0].clone();

        for (int i = 1; i < inputFrames.length; i++) {
            if (inputFrames[i - 1].channels() != inputFrames[i].channels()) throw new IllegalArgumentException("Input frames of mixed channel types are not supported");

            Core.bitwise_and(outputFrame, inputFrames[i], outputFrame);
        }

        return outputFrame;
    }
}
