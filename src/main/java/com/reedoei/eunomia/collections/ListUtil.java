package com.reedoei.eunomia.collections;

import com.reedoei.eunomia.util.Util;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: Add collection casting functions!!!
// TODO: Move and deprecate functions from Util.

public class ListUtil {
    public static List<Integer> range(final int end) {
        return range(0, end);
    }

    public static List<Integer> range(final int start, final int end) {
        return range(start, end, 1);
    }

    public static List<Integer> range(final int start, final int end, final int step) {
        final List<Integer> result = new ArrayList<>();

        for (int i = start; i < end; i += step) {
            result.add(i);
        }

        return result;
    }

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

    public static @NonNull List<String> read(@NonNull final String s) {
        return read(s, Function.identity());
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

    public static <T> List<T> beforeInc(final List<T> ts, final @NonNull T t) {
        final int i = ts.indexOf(t);

        if (i != -1) {
            return new ArrayList<>(ts.subList(0, Math.min(ts.size(), i + 1)));
        } else {
            return new ArrayList<>();
        }
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

        while (!result.isEmpty()) {
            if (pred.test(result.get(0))) {
                result.remove(0);
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

        return new ArrayList<>(ts.subList(Math.min(n, ts.size()), ts.size()));
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
    public static <T> List<@NonNull T> fromArray(final T... ts) {
        final List<@NonNull T> result = new ArrayList<>();

        for (final T t : ts) {
            if (t != null) {
                result.add(t);
            }
        }

        return result;
    }

    @SafeVarargs
    public static <T> List<@Nullable T> fromArrayUnsafe(final T... ts) {
        return new ArrayList<>(Arrays.asList(ts));
    }

    public static <T> List<T> collect(final Iterable<T> it) {
        return collect(it.iterator());
    }

    public static <T> List<T> collect(final Iterator<T> it) {
        final List<T> result = Collections.synchronizedList(new ArrayList<>());

        it.forEachRemaining(result::add);

        return result;
    }

    public static <T> Stream<List<T>> subsequences(final List<T> list) {
        if (list.isEmpty()) {
            return Stream.empty();
        }

        final T x = list.get(0);
        final List<T> xs = list.subList(1, list.size());

        return Stream.concat(
                Stream.of(new ArrayList<>(Collections.singletonList(x))),
                subsequences(xs)
                        .reduce(Stream.empty(), (Stream<List<T>> r, List<T> ys) -> {
                            List<T> temp = new ArrayList<>(ys);
                            temp.add(0, x);

                            return Stream.concat(Stream.concat(Stream.of(ys), Stream.of(temp)), r);
                        }, Stream::concat));
    }

    public static <T> List<List<T>> permutations(final List<T> ts) {
        if (ts.isEmpty()) {
            return ListUtil.fromArray(new ArrayList<>());
        }

        final List<List<T>> result = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < ts.size(); i++) {
            final List<T> newTs = new ArrayList<>(ts);
            newTs.remove(i);

            for (final List<T> seq : permutations(newTs)) {
                result.add(Util.prepend(ts.get(i), seq));
            }
        }

        return result;
    }

    public static <T> List<T> sample(final Stream<T> ts, final int n) {
        return sample(ts.collect(Collectors.toList()), n);
    }

    public static <T> List<T> sample(final List<T> ts, final int n) {
        final List<T> temp = Collections.synchronizedList(new ArrayList<>(ts));
        Collections.shuffle(temp);
        return ListUtil.take(n, temp);
    }

    public static <T> List<T> ensureCapacity(final List<T> ts, final List<T> other, final T t) {
        return ensureCapacity(ts, other.size(), () -> t);
    }

    public static <T> List<T> ensureCapacity(final List<T> ts, final List<T> other, final Supplier<T> supplier) {
        return ensureCapacity(ts, other.size(), supplier);
    }

    public static <T> List<T> ensureCapacity(final List<T> ts, final int n, final Supplier<T> supplier) {
        while (ts.size() < n) {
            ts.add(supplier.get());
        }

        return ts;
    }
}
