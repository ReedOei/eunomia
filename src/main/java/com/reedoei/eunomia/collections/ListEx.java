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
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
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

    public static <T> ListEx<T> newList(final int n, final Class<T> clz) {
        return newList(n, () -> new RuntimeThrower<>(clz::newInstance).run());
    }

    public static <T> ListEx<T> newList(final int n, final T t) {
        return newList(n, () -> t);
    }

    public static <T> ListEx<T> newList(final int n, final Supplier<T> supplier) {
        return new ListEx<T>().ensureSize(n, supplier);
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

    public static <T> ListEx<T> collect(final Stream<T> s) {
        return s.collect(Collectors.toCollection(ListEx::new));
    }

    public static <T> ListEx<T> collect(final Iterable<T> it) {
        return collect(it.iterator());
    }

    public static <T> ListEx<T> collect(final Iterator<T> it) {
        final ListEx<T> result = new ListEx<>();

        it.forEachRemaining(result::add);

        return result;
    }

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

    public ListEx<T> reversed() {
        final ListEx<T> result = new ListEx<>(this);
        Collections.reverse(result);
        return result;
    }

    public <U> ListEx<T> distinctBy(final Function<T, U> f) {
        return stream().filter(Util.distinctByKey(f)).collect(Collectors.toCollection(ListEx::new));
    }

    public ListEx<T> distinct() {
        return distinctBy(Function.identity());
    }

    public ListEx<ListEx<T>> groupBy(final BiPredicate<T, T> f) {
        final ListEx<ListEx<T>> result = new ListEx<>();

        return result;
    }

    public ListEx<T> beforeInc(final @NonNull T t) {
        final int i = indexOf(t);

        if (i != -1) {
            return new ListEx<>(subList(0, Math.min(size(), i + 1)));
        } else {
            return new ListEx<>();
        }
    }

    public ListEx<T> before(final T t) {
        final int i = indexOf(t);

        if (i != -1) {
            return new ListEx<>(subList(0, Math.min(size(), i)));
        } else {
            return new ListEx<>();
        }
    }

    public ListEx<T> afterInc(final T t) {
        final int i = indexOf(t);

        if (i != -1) {
            return new ListEx<>(subList(Math.min(size(), i), size()));
        } else {
            return new ListEx<>();
        }
    }

    public ListEx<T> after(final T t) {
        final int i = indexOf(t);

        if (i != -1) {
            return new ListEx<>(subList(Math.min(size(), i + 1), size()));
        } else {
            return new ListEx<>();
        }
    }

    public ListEx<T> take(final int n) {
        return new ListEx<>(subList(0, Math.min(n, size())));
    }

    public ListEx<T> takeWhileInc(final Predicate<T> f) {
        final ListEx<T> result = new ListEx<>();

        for (final T t : this) {
            result.add(t);
            if (!f.test(t)) {
                break;
            }
        }

        return result;
    }

    public ListEx<T> takeWhile(final Predicate<T> pred) {
        final ListEx<T> result = new ListEx<>();

        for (final T t : this) {
            if (pred.test(t)) {
                result.add(t);
            } else {
                break;
            }
        }

        return result;
    }

    public ListEx<T> dropWhile(final Predicate<T> pred) {
        final ListEx<T> result = new ListEx<>(this);

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

    public ListEx<T> tail() {
        return drop(1);
    }

    public Stream<ListEx<T>> subsequences() {
        if (isEmpty()) {
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
                return i < size() && (j < sequences.size() || i + 1 < size());
            }

            @Override
            public ListEx<T> next() {
                // We're done now...
                if (i < size()) {
                    if (j >= sequences.size()) {
                        sequences.addAll(newSequences);
                        newSequences.clear();
                        j = 0;
                        i++;
                    }

                    prev = new ListEx<>(sequences.get(j));
                    prev.add(get(i));
                    newSequences.add(prev);
                    j++;
                }

                return prev;
            }
        });
    }

    public Stream<ListEx<T>> permutations() {
        if (isEmpty()) {
            return Stream.of(new ListEx<>());
        }

        return tail().permutations().flatMap(p -> stream().map(p::prepend));
    }

    private ListEx<T> prepend(final T t) {
        final ListEx<T> result = new ListEx<>(this);

        result.add(0, t);

        return result;
    }

    public ListEx<T> sample(final int n) {
        final ListEx<T> temp = new ListEx<>(this);
        Collections.shuffle(temp);
        return temp.take(n);
    }

    public ListEx<T> sampleWithReplacement(final int n) {
        final ListEx<T> result = new ListEx<>();

        final Random random = new Random();

        for (int i = 0; i < n; i++) {
            result.add(get(random.nextInt(size())));
        }

        return result;
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
