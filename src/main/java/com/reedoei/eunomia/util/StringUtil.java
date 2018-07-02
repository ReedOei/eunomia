package com.reedoei.eunomia.util;

import com.reedoei.eunomia.collections.ListUtil;

import java.util.List;

// Should use the one from string package.
@Deprecated
public class StringUtil {
    public static List<String> lines(final String s) {
        return ListUtil.fromArray(s.split(System.lineSeparator()));
    }

    public static String unlines(final List<String> lines) {
        return String.join(System.lineSeparator(), lines);
    }

    public static List<String> words(final String s) {
        return ListUtil.fromArray(s.split(" "));
    }

    public static String unwords(final List<String> words) {
        return String.join(" ", words);
    }
}
