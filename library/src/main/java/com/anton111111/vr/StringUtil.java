package com.anton111111.vr;

import java.util.Locale;

public class StringUtil {

    public static String formatMatrix(float[] matrix, int cols) {
        String r = "[";
        int c = 0;
        for (int i = 0; i < matrix.length; i++) {
            r += String.format(Locale.US, "%.6f", matrix[i]);
            if (i < matrix.length - 1) {
                r += ",";
            }
            c++;
            if (i != matrix.length - 1) {
                r += (c == cols) ? "\n" : " ";
                if (c == cols) {
                    c = 0;
                }
            }
        }
        r += "]";
        return r;
    }
}
