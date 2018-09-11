package com.zuhlke.apparel.measurement.pdftable.converter.processor;

import org.opencv.core.Mat;

public interface Processor {
    Mat apply(Mat... inputFrames);
}
