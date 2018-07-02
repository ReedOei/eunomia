package com.reedoei.eunomia.string;

public class CharUtil {
    private static final String punctuation = ".,;:[]()-!?";

    public static boolean isChar(final char c) {
        return c != ' ' && c != '\t' && c != '\r' && c != '\n' && c >= ' ';
    }

    public static boolean isPunctuation(char c) {
        for (final char punc : punctuation.toCharArray()) {
            if (c == punc) {
                return true;
            }
        }

        return false;
    }
}
