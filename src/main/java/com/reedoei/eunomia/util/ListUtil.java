package com.reedoei.eunomia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListUtil {
    @NonNull
    public static <T> List<T> concat(final List<T> a, final List<T> b) {
        if (a == null || b == null) {
            return new ArrayList<>();
        } else {
            a.addAll(b);
            return a;
        }
    }

    @NonNull
    public static Function<String, List<String>> reader() {
        return reader(Function.identity());
    }

    @NonNull
    public static <T> Function<String, List<T>> reader(@NonNull final Function<String, T> constructor) {
        return s -> read(s, constructor);
    }

    @NonNull
    public static <T> List<T> read(@NonNull final String s,
                                   @NonNull final Function<String, T> constructor) {
        return Arrays.stream(s
                .replace("[", "")
                .replace("]", "")
                .split(","))
                .map(String::trim)
                .map(constructor)
                .collect(Collectors.toList());
    }

    public static <T> Function<List<T>, List<T>> take(final int n) {
        return ts -> take(n, ts);
    }

    public static <T> List<T> take(final int n, final List<T> ts) {
        if (ts == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(ts.subList(0, Math.min(n, ts.size())));
    }

    public static <T> Function<List<T>, List<T>> takeWhileInc(final Predicate<T> f) {
        return ts -> takeWhileInc(f, ts);
    }

    public static <T> List<T> takeWhileInc(final Predicate<T> f, final List<T> ts) {
        final List<T> result = new ArrayList<>();

        for (final T t : ts) {
            result.add(t);
            if (!f.test(t)) {
                break;
            }
        }

        return result;
    }

    public static <T> Function<List<T>, List<T>> takeWhile(final Predicate<T> pred) {
        return ts -> takeWhile(pred, ts);
    }

    public static <T> List<T> takeWhile(final Predicate<T> pred, final List<T> ts) {
        final List<T> result = new ArrayList<>();

        for (final T t : ts) {
            if (pred.test(t)) {
                result.add(t);
            } else {
                break;
            }
        }

        return result;
    }

    public static <T> Function<List<T>, List<T>> dropWhile(final Predicate<T> pred) {
        return ts -> dropWhile(pred, ts);
    }

    public static <T> List<T> dropWhile(final Predicate<T> pred, final List<T> ts) {
        final List<T> result = new ArrayList<>(ts);

        while (!ts.isEmpty()) {
            if (pred.test(ts.get(0))) {
                ts.remove(0);
            } else {
                break;
            }
        }

        return result;
    }

    public static <T> Function<List<T>, List<T>> drop(final int n) {
        return ts -> drop(n, ts);
    }

    public static <T> List<T> drop(final int n, final List<T> ts) {
        if (ts == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(ts.subList(Math.max(n, ts.size()), ts.size()));
    }

    public static List<Double> fromArray(final double[] doubles) {
        return Arrays.stream(doubles).boxed().collect(Collectors.toList());
    }

    public static List<Integer> fromArray(final int[] ints) {
        return Arrays.stream(ints).boxed().collect(Collectors.toList());
    }

    public static List<Long> fromArray(final long[] longs) {
        return Arrays.stream(longs).boxed().collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> List<T> fromArray(final T... ts) {
        final List<T> result = new ArrayList<>();

        for (final T t : ts) {
            if (t != null) {
                result.add(t);
            }
        }

        return result;
    }

    @SafeVarargs
    public static <T> List<T> fromArrayUnsafe(final T... ts) {
        return new ArrayList<>(Arrays.asList(ts));
    }
}
