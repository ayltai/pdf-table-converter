package com.zuhlke.apparel.measurement.pdftable.converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import com.zuhlke.apparel.measurement.pdftable.converter.model.Cell;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Grid;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Rect;
import com.zuhlke.apparel.measurement.pdftable.converter.processor.ColorToGrayProcessor;
import com.zuhlke.apparel.measurement.pdftable.converter.processor.ExternalContourDetector;
import com.zuhlke.apparel.measurement.pdftable.converter.processor.GrayToBinaryProcessor;
import com.zuhlke.apparel.measurement.pdftable.converter.processor.HorizontalLineDetector;
import com.zuhlke.apparel.measurement.pdftable.converter.processor.JointDetector;
import com.zuhlke.apparel.measurement.pdftable.converter.processor.Processor;
import com.zuhlke.apparel.measurement.pdftable.converter.processor.SuperpositionProcessor;
import com.zuhlke.apparel.measurement.pdftable.converter.processor.VerticalLineDetector;
import com.zuhlke.apparel.measurement.pdftable.converter.util.Grids;
import com.zuhlke.apparel.measurement.pdftable.converter.util.Points;

final class PdfToGirdConverter {
    private static final int MIN_CELL_WIDTH  = 12;
    private static final int MIN_CELL_HEIGHT = 12;

    public static void main(final String[] args) throws Exception {
        final long startTime = System.currentTimeMillis();
        PdfToGirdConverter.convert(args[0], true);
        System.out.println("Conversion time: " + String.valueOf((System.currentTimeMillis() - startTime) / 1000d));
    }

    public static List<Grid> convert(final String pdfPath) throws IOException, InterruptedException {
        return PdfToGirdConverter.convert(pdfPath, false);
    }

    static List<Grid> convert(final String pdfPath, final boolean debug) throws IOException, InterruptedException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        final File   pdfFile       = new File(pdfPath);
        final File   directory     = pdfFile.getParentFile();
        final String fileName      = pdfFile.getName().substring(0, pdfFile.getName().length() - 4);
        final String textPath      = new File(directory, fileName + ".txt").getAbsolutePath();
        final String pngPathPrefix = new File(directory, fileName).getAbsolutePath();

        final long startTime = System.currentTimeMillis();
        PdfToPngConverter.convert(pdfPath, pngPathPrefix);
        if (debug) System.out.println(String.format("Step 0 (%s): Convert PDF to image(s)", String.valueOf((System.currentTimeMillis() - startTime) / 1000d)));

        final List<Processor> processors = new ArrayList<>();
        processors.add(ColorToGrayProcessor.builder().build());
        processors.add(GrayToBinaryProcessor.builder()
            .maxValue(255)
            .blockSize(13) // Recommended range is [9, 21]
            .meanOffset(-1) // Recommended range is [-2, 0]
            .build());
        processors.add(HorizontalLineDetector.builder()
            .scale(16) // Recommended range is [16, 96]
            .iteration(1) // Recommended range is [1, 2]
            .build());
        processors.add(VerticalLineDetector.builder()
            .scale(16) // Recommended range is [16, 96]
            .iteration(1) // Recommended range is [1, 2]
            .build());
        processors.add(SuperpositionProcessor.builder().build());
        processors.add(JointDetector.builder().build());
        processors.add(ExternalContourDetector.builder()
            .minArea(300 * 300)
            .minJoints(4)
            .epsilon(3)
            .build());

        final List<Grid> grids = new ArrayList<>();

        int page = 0;

        while (true) {
            page++;

            final List<Point> points = PdfToGirdConverter.findTableJointPoints(directory, fileName, pngPathPrefix, page, processors, debug);
            if (points == null) break;
            if (points.isEmpty()) continue;

            final Grid grid = new Grid();

            if (debug) System.out.println("Step 8: Read text in grid cells");

            for (final Point point : points) {
                final Point right = PdfToGirdConverter.findNextPointHorizontally(points, point);
                if (right == null) continue;
                if (right.x - point.x < PdfToGirdConverter.MIN_CELL_WIDTH) continue;

                final Point bottom = PdfToGirdConverter.findNextPointVertically(points, point);
                if (bottom == null) continue;
                if (bottom.y - point.y < PdfToGirdConverter.MIN_CELL_HEIGHT) continue;

                final Rect rect = new Rect((int)point.x, (int)point.y, (int)(right.x - point.x), (int)(bottom.y - point.y));
                PdfToTextConverter.convert(pdfPath, textPath, page, rect);

                final byte[] bytes = Files.readAllBytes(Paths.get(textPath));
                if (bytes.length == 0) continue;

                final String text = new String(bytes).trim();
                if (text.isEmpty()) continue;

                grid.getCells().add(new Cell(rect, PdfToGirdConverter.clean(text)));
            }

            if (!grid.getCells().isEmpty()) grids.add(grid);
            if (debug) System.out.println(grid.toString());

            new File(textPath).delete();
        }

        return grids;
    }

    private static List<Point> findTableJointPoints(final File directory, final String fileName, final String pngPathPrefix, final int page, final List<Processor> processors, final boolean debug) {
        long startTime = 0;

        final String pngPath = pngPathPrefix + "-" + String.valueOf(page) + ".png";
        if (!new File(pngPath).exists()) return null;

        if (debug) System.out.println("Processing page " + String.valueOf(page));

        if (debug) startTime = System.currentTimeMillis();
        final Mat grayFrame = processors.get(0).apply(PngToMatrixConverter.convert(pngPath));
        if (!debug) new File(pngPath).delete();
        if (debug) {
            System.out.println("Step 1 (" + String.valueOf((System.currentTimeMillis() - startTime) / 1000d) + "): Converted color image to grayscale");
            MatrixToPngConverter.convert(grayFrame, new File(directory, "1-gray-" + fileName + "-" + String.valueOf(page) + ".png").getAbsolutePath());
        }

        if (debug) startTime = System.currentTimeMillis();
        final Mat binaryFrame = processors.get(1).apply(grayFrame);
        if (debug) {
            System.out.println("Step 2 (" + String.valueOf((System.currentTimeMillis() - startTime) / 1000d) + "): Converted grayscale image to binary");
            MatrixToPngConverter.convert(binaryFrame, new File(directory, "2-binary-" + fileName + "-" + String.valueOf(page) + ".png").getAbsolutePath());
        }

        if (debug) startTime = System.currentTimeMillis();
        final Mat horizontalLines = processors.get(2).apply(binaryFrame);
        if (debug) {
            System.out.println("Step 3 (" + String.valueOf((System.currentTimeMillis() - startTime) / 1000d) + "): Found horizontal lines");
            MatrixToPngConverter.convert(horizontalLines, new File(directory, "3-horizontal-" + fileName + "-" + String.valueOf(page) + ".png").getAbsolutePath());
        }

        if (debug) startTime = System.currentTimeMillis();
        final Mat verticalLines = processors.get(3).apply(binaryFrame);
        if (debug) {
            System.out.println("Step 4 (" + String.valueOf((System.currentTimeMillis() - startTime) / 1000d) + "): Found vertical lines");
            MatrixToPngConverter.convert(verticalLines, new File(directory, "4-vertical-" + fileName + "-" + String.valueOf(page) + ".png").getAbsolutePath());
        }

        if (debug) startTime = System.currentTimeMillis();
        final Mat maskFrame = processors.get(4).apply(horizontalLines, verticalLines);
        if (debug) {
            System.out.println("Step 5 (" + String.valueOf((System.currentTimeMillis() - startTime) / 1000d) + "): Found table lines");
            MatrixToPngConverter.convert(maskFrame, new File(directory, "5-mask-" + fileName + "-" + String.valueOf(page) + ".png").getAbsolutePath());
        }

        if (debug) startTime = System.currentTimeMillis();
        final Mat jointFrame = processors.get(5).apply(horizontalLines, verticalLines);
        if (debug) {
            System.out.println("Step 6 (" + String.valueOf((System.currentTimeMillis() - startTime) / 1000d) + "): Found table line joint points");
            MatrixToPngConverter.convert(jointFrame, new File(directory, "6-joints-" + fileName + "-" + String.valueOf(page) + ".png").getAbsolutePath());
        }

        if (debug) startTime = System.currentTimeMillis();
        final Mat regionFrame = processors.get(6).apply(maskFrame, jointFrame);
        if (regionFrame == null) return Collections.emptyList();
        if (debug) System.out.println("Step 7 (" + String.valueOf((System.currentTimeMillis() - startTime) / 1000d) + "): Found table boundary");

        final List<Point> points = Points.toPoints(regionFrame);
        Points.sort(points);

        return points;
    }

    private static Point findNextPointHorizontally(final List<Point> points, final Point point) {
        for (int i = points.indexOf(point) + 1; i < points.size(); i++) {
            final Point next = points.get(i);
            if ((int)next.x > (int)point.x && Grids.roughlyEquals((int)next.y, (int)point.y)) return next;
        }

        return null;
    }

    private static Point findNextPointVertically(final List<Point> points, final Point point) {
        for (int i = points.indexOf(point) + 1; i < points.size(); i++) {
            final Point next = points.get(i);
            if ((int)next.y > (int)point.y && Grids.roughlyEquals((int)next.x, (int)point.x)) return next;
        }

        return null;
    }

    private static String clean(String text) {
        while (text.contains("\n")) text = text.replace('\n', ' ');
        while (text.contains("  ")) text = text.replace("  ", " ");

        return text;
    }
}
