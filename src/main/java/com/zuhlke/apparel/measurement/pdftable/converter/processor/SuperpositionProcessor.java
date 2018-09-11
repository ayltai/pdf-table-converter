package com.zuhlke.apparel.measurement.pdftable.converter.processor;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public final class SuperpositionProcessor implements Processor {
    public static final class Builder {
        private Builder() {
        }

        public SuperpositionProcessor build() {
            return new SuperpositionProcessor();
        }
    }

    public static SuperpositionProcessor.Builder builder() {
        return new SuperpositionProcessor.Builder();
    }

    private SuperpositionProcessor() {
    }

    @Override
    public Mat apply(final Mat... inputFrames) {
        if (inputFrames == null) throw new NullPointerException("Input frames are null");
        if (inputFrames.length == 1) return inputFrames[0];

        final Mat outputFrame = inputFrames[0].clone();

        for (int i = 1; i < inputFrames.length; i++) {
            if (inputFrames[i - 1].channels() != inputFrames[i].channels()) throw new IllegalArgumentException("Input frames of mixed channel types are not supported");

            Core.add(outputFrame, inputFrames[1], outputFrame);
        }

        return outputFrame;
    }
}
