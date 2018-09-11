package com.zuhlke.apparel.measurement.pdftable.converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.zuhlke.apparel.measurement.pdftable.converter.model.Cell;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Row;
import com.zuhlke.apparel.measurement.pdftable.converter.model.Table;
import com.zuhlke.apparel.measurement.pdftable.converter.util.Strings;

final class TableToHtmlConverter {
    private TableToHtmlConverter() {
    }

    public static void convert(final List<Table> tables, final String htmlPathPrefix) throws IOException {
        if (tables == null) return;

        for (int i = 0; i < tables.size(); i++) {
            final Table         table   = tables.get(i);
            final StringBuilder builder = new StringBuilder();

            if (!table.isEmpty()) {
                builder.append("<tr>");
                builder.append("<td>POM</td>");
                builder.append("<td>DESCRIPTION</td>");

                for (final String name : table.getRows().get(0).getSizeNames()) builder.append("<td>").append(name).append("</td>");

                builder.append("<td>TOL-</td>");
                builder.append("<td>TOL+</td>");
                builder.append("</tr>");

                for (final Row row : table) {
                    builder.append("<tr>");

                    builder.append("<td>");
                    if (row.getCode() != null && !Strings.isNullOrEmpty(row.getCode().getText())) builder.append(row.getCode().getText());
                    builder.append("</td>");

                    builder.append("<td>");
                    if (row.getDescription() != null && !Strings.isNullOrEmpty(row.getDescription().getText())) builder.append(row.getDescription().getText());
                    builder.append("</td>");

                    for (final String name : table.getRows().get(0).getSizeNames()) {
                        builder.append("<td>");

                        final Cell cell = row.getSize(name);
                        if (cell != null && !Strings.isNullOrEmpty(cell.getText())) builder.append(cell.getText());

                        builder.append("</td>");
                    }

                    builder.append("<td>");
                    if (row.getNegativeTolerance() != null && !Strings.isNullOrEmpty(row.getNegativeTolerance().getText())) builder.append(row.getNegativeTolerance().getText());
                    builder.append("</td>");

                    builder.append("<td>");
                    if (row.getPositiveTolerance() != null && !Strings.isNullOrEmpty(row.getPositiveTolerance().getText())) builder.append(row.getPositiveTolerance().getText());
                    builder.append("</td>");

                    builder.append("</tr>");
                }

                builder.insert(0, "<html><body><table cellpadding=3 border=1 style=border-collapse:collapse;>");
                builder.append("</table></body></html>");

                Files.write(Paths.get(htmlPathPrefix + "-" + String.valueOf(i) + ".html"), builder.toString().getBytes());
            }
        }
    }
}
