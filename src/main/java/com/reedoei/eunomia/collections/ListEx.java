package com.reedoei.eunomia.collections;

import com.google.common.collect.Streams;
import com.reedoei.eunomia.util.RuntimeThrower;
import com.reedoei.eunomia.util.Util;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListEx<T> extends ArrayList<T> {
    public ListEx(final List<T> ts) {
        super(ts);
    }

    public ListEx() {
        super();
    }

//    public static <T> ListEx<T> newList(final int n, final Class<T> clz) {
//        return ensureSize(Collections.synchronizedList(new ArrayList<>()), n,
//                () -> new RuntimeThrower<>(clz::newInstance).run());
//    }
//
//    public static <T> ListEx<T> newList(final int n, final T t) {
//        return ensureSize(Collections.synchronizedList(new ArrayList<>()), n, () -> t);
//    }
//
//    public static <T> ListEx<T> newList(final int n, final Supplier<T> supplier) {
//        return ensureSize(Collections.synchronizedList(new ArrayList<>()), n, supplier);
//    }

    public static ListEx<Integer> range(final int end) {
        return range(0, end);
    }

    public static ListEx<Integer> range(final int start, final int end) {
        return range(start, end, 1);
    }

    public static ListEx<Integer> range(final int start, final int end, final int step) {
        final ListEx<Integer> result = new ListEx<>();

        for (int i = start; i < end; i += step) {
            result.add(i);
        }

        return result;
    }

    public static <T> ListEx<T> concat(final ListEx<T> a, final ListEx<T> b) {
        if (a == null && b == null) {
            return new ListEx<>();
        } else if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            a.addAll(b);
            return a;
        }
    }

    public static Function<String, ListEx<String>> reader() {
        return reader(Function.identity());
    }

    public static <T> Function<String, ListEx<T>> reader(final Function<String, T> constructor) {
        return s -> new ListEx<>(ListUtil.read(constructor, s));
    }

    public static ListEx<String> read(final String s) {
        return new ListEx<>(ListUtil.read(s));
    }

    public static <T> ListEx<T> read(final Function<String, T> constructor,
                                   final String s) {
        return new ListEx<>(ListUtil.read(constructor, s));
    }

    public static <T> List<T> beforeInc(final List<T> ts, final @NonNull T t) {
        final int i = ts.indexOf(t);

        if (i != -1) {
            return new ArrayList<>(ts.subList(0, Math.min(ts.size(), i + 1)));
        } else {
            return new ArrayList<>();
        }
    }

    public static <T> List<T> before(final List<T> ts, final T t) {
        final int i = ts.indexOf(t);

        if (i != -1) {
            return new ArrayList<>(ts.subList(0, Math.min(ts.size(), i)));
        } else {
            return new ArrayList<>();
        }
    }

    public static <T> List<T> afterInc(final List<T> ts, final T t) {
        final int i = ts.indexOf(t);

        if (i != -1) {
            return new ArrayList<>(ts.subList(Math.min(ts.size(), i), ts.size()));
        } else {
            return new ArrayList<>();
        }
    }

    public static <T> List<T> after(final List<T> ts, final T t) {
        final int i = ts.indexOf(t);

        if (i != -1) {
            return new ArrayList<>(ts.subList(Math.min(ts.size(), i + 1), ts.size()));
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

    public ListEx<T> drop(final int n) {
        return new ListEx<>(subList(Math.min(n, size()), size()));
    }

    public static ListEx<Double> fromArray(final double[] doubles) {
        ListEx<Double> list = new ListEx<>();
        for (double v : doubles) {
            Double aDouble = v;
            list.add(aDouble);
        }
        return list;
    }

    public static ListEx<Integer> fromArray(final int[] ints) {
        ListEx<Integer> list = new ListEx<>();
        for (int i : ints) {
            Integer integer = i;
            list.add(integer);
        }
        return list;
    }

    public static ListEx<Long> fromArray(final long[] longs) {
        ListEx<Long> list = new ListEx<>();
        for (long l : longs) {
            Long aLong = l;
            list.add(aLong);
        }
        return list;
    }

    @SafeVarargs
    public static <T> ListEx<@NonNull T> fromArray(final T... ts) {
        final ListEx<@NonNull T> result = new ListEx<>();

        for (final T t : ts) {
            if (t != null) {
                result.add(t);
            }
        }

        return result;
    }

    @SafeVarargs
    public static <T> ListEx<@Nullable T> fromArrayUnsafe(final T... ts) {
        return new ListEx<>(Arrays.asList(ts));
    }

    public static <T> ListEx<T> collect(final Iterable<T> it) {
        return collect(it.iterator());
    }

    public static <T> ListEx<T> collect(final Iterator<T> it) {
        final ListEx<T> result = new ListEx<>();

        it.forEachRemaining(result::add);

        return result;
    }

    public static <T> Stream<ListEx<T>> subsequences(final List<T> list) {
        if (list.isEmpty()) {
            return Stream.empty();
        }

        return Streams.stream(new Iterator<ListEx<T>>() {
            private final List<ListEx<T>> sequences =
                    Collections.synchronizedList(new ArrayList<>(Collections.singletonList(new ListEx<>())));
            private final List<ListEx<T>> newSequences = Collections.synchronizedList(new ArrayList<>());

            private ListEx<T> prev = new ListEx<>();

            private int i = 0;
            private int j = 0;

            @Override
            public boolean hasNext() {
                return i < list.size() && (j < sequences.size() || i + 1 < list.size());
            }

            @Override
            public ListEx<T> next() {
                // We're done now...
                if (i < list.size()) {
                    if (j >= sequences.size()) {
                        sequences.addAll(newSequences);
                        newSequences.clear();
                        j = 0;
                        i++;
                    }

                    prev = new ListEx<>(sequences.get(j));
                    prev.add(list.get(i));
                    newSequences.add(prev);
                    j++;
                }

                return prev;
            }
        });
    }

//    public static <T> ListEx<ListEx<T>> permutations(final List<T> ts) {
//        if (ts.isEmpty()) {
//            return ListUtil.fromArray(new ArrayList<>());
//        }
//
//        final List<List<T>> result = Collections.synchronizedList(new ArrayList<>());
//
//        for (int i = 0; i < ts.size(); i++) {
//            final List<T> newTs = new ArrayList<>(ts);
//            newTs.remove(i);
//
//            for (final List<T> seq : permutations(newTs)) {
//                result.add(Util.prepend(ts.get(i), seq));
//            }
//        }
//
//        return result;
//    }

    public static <T> List<T> sample(final Stream<T> ts, final int n) {
        return sample(ts.collect(Collectors.toList()), n);
    }

    public static <T> List<T> sample(final List<T> ts, final int n) {
        final List<T> temp = Collections.synchronizedList(new ArrayList<>(ts));
        Collections.shuffle(temp);
        return ListUtil.take(n, temp);
    }

    public ListEx<T> ensureSize(final List<T> other, final Class<T> clz) {
        return ensureSize(other.size(), () -> new RuntimeThrower<>(clz::newInstance).run());
    }

    public ListEx<T> ensureSize(final List<T> other, final T t) {
        return ensureSize(other.size(), () -> t);
    }

    public ListEx<T> ensureSize(final List<T> other, final Supplier<T> supplier) {
        return ensureSize(other.size(), supplier);
    }

    public ListEx<T> ensureSize(final int n, final Supplier<T> supplier) {
        while (size() < n) {
            add(supplier.get());
        }

        return this;
    }

    public <U> ListEx<U> mapWithIndex(final BiFunction<Integer, T, U> f)  {
        final ListEx<U> result = new ListEx<>();

        for (int i = 0; i < size(); i++) {
            result.add(f.apply(i, get(i)));
        }

        return result;
    }

    public <U> ListEx<U> map(final Function<T, U> f) {
        final ListEx<U> result = new ListEx<>();

        for (final T t : this) {
            result.add(f.apply(t));
        }

        return result;
    }

    public ListEx<T> filter(final Predicate<T> pred) {
        final ListEx<T> result = new ListEx<>();

        for (final T t : this) {
            if (pred.test(t)) {
                result.add(t);
            }
        }

        return result;
    }
}
