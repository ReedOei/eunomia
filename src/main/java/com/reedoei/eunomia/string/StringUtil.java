package com.reedoei.eunomia.string;

import com.reedoei.eunomia.collections.ListUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

    public static Optional<Character> lastChar(final String s) {
        for (int i = s.length() - 1; i >= 0; i--) {
            if (CharUtil.isChar(s.charAt(i))) {
                return Optional.of(s.charAt(i));
            }
        }

        return Optional.empty();
    }

    public static Optional<Character> firstChar(final String s) {
        for (int i = 0; i < s.length(); i++) {
            if (CharUtil.isChar(s.charAt(i))) {
                return Optional.of(s.charAt(i));
            }
        }

        return Optional.empty();
    }

    public static String filter(final Predicate<Character> pred, final String s) {
        final StringBuilder result = new StringBuilder();

        for (final char c : s.toCharArray()) {
            if (pred.test(c)) {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static String removeLast(final Predicate<Character> pred, final String s) {
        for (int i = s.length() - 1; i >= 0; i--) {
            if (pred.test(s.charAt(i))) {
                return s.substring(0, i) + s.substring(i + 1);
            }
        }

        return s;
    }

    public static String remove(final Predicate<Character> pred, final String s) {
        for (int i = 0; i < s.length(); i++) {
            if (pred.test(s.charAt(i))) {
                return s.substring(0, i) + s.substring(i + 1);
            }
        }

        return s;
    }

    public static String set(final String s, final int i, final char c) {
        final StringBuilder result = new StringBuilder(s);
        result.setCharAt(i, c);
        return result.toString();
    }
}
