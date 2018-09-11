package com.zuhlke.apparel.measurement.pdftable.converter.processor;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;

public final class ExternalContourDetector implements Processor {
    private static double MIN_AREA   = 500 * 500;
    private static int    MIN_JOINTS = 4;
    private static double EPSILON    = 3;

    public static final class Builder {
        private double minArea   = ExternalContourDetector.MIN_AREA;
        private int    minJoints = ExternalContourDetector.MIN_JOINTS;
        private double epsilon   = ExternalContourDetector.EPSILON;

        private Builder() {
        }

        public ExternalContourDetector.Builder minArea(final double minArea) {
            this.minArea = minArea;
            return this;
        }

        public ExternalContourDetector.Builder minJoints(final int minJoints) {
            this.minJoints = minJoints;
            return this;
        }

        public ExternalContourDetector.Builder epsilon(final double epsilon) {
            this.epsilon = epsilon;
            return this;
        }

        public ExternalContourDetector build() {
            return new ExternalContourDetector(this.minArea, this.minJoints, this.epsilon);
        }
    }

    private final double minArea;
    private final int    minJoints;
    private final double epsilon;

    public static ExternalContourDetector.Builder builder() {
        return new ExternalContourDetector.Builder();
    }

    private ExternalContourDetector(final double minArea, final int minJoints, final double epsilon) {
        this.minArea   = minArea;
        this.minJoints = minJoints;
        this.epsilon   = epsilon;
    }

    @Override
    public Mat apply(final Mat... inputFrames) {
        if (inputFrames == null) throw new NullPointerException("Input frames are null");
        if (inputFrames.length != 2) throw new IllegalArgumentException("Two and only two input frames are required");
        if (inputFrames[0].channels() != 1 || inputFrames[1].channels() != 1) throw new IllegalArgumentException("Only single-channel input frames are supported");

        final List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(inputFrames[0], contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (final MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) < this.minArea) continue;

            final MatOfPoint2f curve = new MatOfPoint2f();
            curve.fromArray(contour.toArray());

            final MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(curve, approxCurve, this.epsilon, true);

            final MatOfPoint points = new MatOfPoint();
            points.fromArray(approxCurve.toArray());

            final List<MatOfPoint> jointContours = new ArrayList<>();
            Imgproc.findContours(inputFrames[1].submat(Imgproc.boundingRect(points)), jointContours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

            if (jointContours.size() > this.minJoints) return inputFrames[1];
        }

        return null;
    }
}
