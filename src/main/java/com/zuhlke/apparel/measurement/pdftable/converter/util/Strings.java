package com.zuhlke.apparel.measurement.pdftable.converter.util;

public final class Strings {
    private Strings() {
    }

    public static boolean isNullOrEmpty(final String string) {
        if (string == null) return true;
        return string.isEmpty();
    }
}
