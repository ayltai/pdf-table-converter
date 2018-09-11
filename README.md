# PDF Tables Converter
A Java 8 executable library for converting tables in PDF files to plain Java Objects or HTML files in a standard format.

## Introduction
This library uses 10 steps to read and process input PDF files and convert them to plain Java Objects or HTML files.

#### Step 1: Convert PDF to PNG images
It calls `pdftoppm` to convert each page to an image. The default DPI is 150. Decreasing the DPI can speed up the conversion process but the conversion quality will also drop. Increasing the DPI does not seem to improve the conversion quality.

#### Step 2: Convert colored image to grayscale
In most cases, color information is not useful so we just use grayscale, which can speed up the whole process significantly.

#### Step 3: Convert grayscale image to binary
The grascale image is first inverted by applying a bitwise NOT operator to each byte value. Then we apply adaptive thresholding to obtain a high quality binary image.

#### Step 4: Detect horizontal lines
Use a morphological operator to detect horizontal lines.

#### Step 5: Detect vertical lines
Use a morphological operator to detect vertical lines.

#### Step 6: Superposition horizontal and vertical lines
Combine the horizontal and vertical lines to reconstruct the table lines.

#### Step 7: Detect joint points
Use bitwise AND operator to detect joint points of the horizontal and vertical lines. Now we have the vertices of all table cells.

#### Step 8: Detect the outmost boundary of the table
Find our region-of-interest by detecting the external contour from the result obtained in step 6.

#### Step 9: Read text in PDF
Within the region-of-interest obtained in step 8, scan through all joint points obtained in step 7 and detect any points that form a rectangular shape. Read the text bounded by each set of rectangular shaped joint points. Now we have a collection text associated with coordinate information.

#### Step 10: Convert the collection of text to standard format
Create a standard format table by using the coordinate information and identifying specific keywords.

## Installation
This library uses the following native commands:
* [`pdftotext` by Poppler](https://poppler.freedesktop.org/): This is a command to read text in PDF. It is the same as [`pdftotext` by xpdf](http://www.xpdfreader.com/) but also supports reading text in PDF at specific coordinates .
* [`pdftoppm` by Poppler](https://poppler.freedesktop.org/): This is a command to convert PDF files to images. The converted image file name will be [pdf-file-name]-[page number].[jpg|png|ppm].
* [OpenCV](https://opencv.org/): It contains a large collection of image processing functions.

### macOS
1. Install [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. Follow [this guide](http://macappstore.org/poppler/) to install Poppler on macOS.
3. Follow [this guide](https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html) to build and install OpenCV for Java on macOS. Note that the build process may take 20 minutes or more.

### Linux
1. Install [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. Download Poppler binaries [here](https://poppler.freedesktop.org/).
3. Follow [this guide](https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html) to build and install OpenCV for Java on Linux. Note that the build process may take 20 minutes or more.

### Windows
1. Install [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. Download Poppler binaries [here](http://blog.alivate.com.au/poppler-windows/).
3. Download OpenCV for Java [here](https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html).

## Parameters
The quality of the conversion depends on how the table looks like physically. There are a few parameters that can be customized to best-fit certain types of tables.

The parameters can be found in `PdfToGridConverter.java`.

#### Block size
The number of pixels to calculate the mean pixel value used for converting grayscale image to binary. Pixel value larger than the mean will be converted to 1 and the rest will be 0.

Recommended range is [9, 21].

#### Mean offset
This value will be added to the mean calculated in step 3. The purpose is to remove noise.

Recommended range is [-2, 0].

#### Line detection scale
The higher this number the shorter lines can be detected. Do not use very large number because this will detect very short lines that are probably not part of a table but an image.

Recommended range is [16, 96].

#### Line detection iteration
More iterations can connect broken table lines but it also thicken the lines.

Recommended range is [1, 2].

#### Minimum table dimension
This defines the minimum table area that is considered to be a valid table. Any rectangular shaped objects with smaller area are discarded as they are probably an image, not table.

Recommended range is [300 x 300, 500 x 500].

## How to run
This library can be executed as a standalone program or as a library. The standalone program is intended to be used for debugging purpose only. Do not use it in production environment.

### As a standalone program
There are 3 executable standalone programs:
1. `PdfToGridConverter.java`: Convert a PDF file to a collection of `Grid` objects.
2. `PdfToTableConverter.java`: Convert a PDF file to a collection of `Table` object.
3. `PdfToHtmlConverter.java`: Convert a PDF file to HTML file(s).

All of the above programs take one argument which is the path of the PDF file to be converted.

Example:
```bash
cd [path to project]/out/artifacts/com_zuhlke_apparel_measurement_pdf_table_converter_jar
java -cp com.zuhlke.apparel.measurement.pdf-table-converter.jar:./* com.zuhlke.apparel.measurement.pdftable.converter.PdfToHtmlConverter [path to PDF file]
```

### As a library
There are 2 callable converters:
1. `PdfToTableConverter.convert(String)`: Convert a PDF file (path is specified by the given parameter) to `Table` object.
2. `PdfToHtmlConverter.convert(String, String)`: Convert a PDF file (path is specified by the 1st parameter) to HTML file(s) (path is specified by the 2nd parameter).

The `Table` object contains a collection of `Row` objects. Each `Row` contains:
* A `code`, also known as "POM"
* A `description`, also known as "POM description"
* Positive tolerance
* Negative tolerance
* A key-value `Map` of `Cell`s that represents the apparel size measurements. The key represents the size label (e.g. S, M, L, or 42, 44, 46, etc) and the value is a `Cell` object that contains the size measurement.

## Issues
1. If the width of the table lines is irregular (e.g. width of header lines is 2-pixel but the rest is 1-pixel), this library does not work well because some table cells position is mis-aligned. Some of the table cells will be missing from the conversion result.
2. If the table is missing some lines (e.g. it may use different color to indicate rows or columns without using lines), this library does not work.
3. If the table uses a dark-color background, this library does not work well because it cannot produce a high quality binary image in step 3.
4. If the table contains invisible text (e.g. white text on white background), this library will also read them all.
5. Program parameters are hardcoded. Ideally, it should be automatically tuned to best-fit the type of table being converted.
