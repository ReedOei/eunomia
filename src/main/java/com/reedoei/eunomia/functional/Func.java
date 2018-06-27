package com.reedoei.eunomia.functional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Func {
    // Exists to make it easier to compose method references.
    public static <T, U, V> Function<T, V> compose(final Function<U, V> g, final Function<T, U> f) {
        return g.compose(f);
    }

    public static <T, U, V> BiFunction<T, U, Pair<V, U>> onlyLeft(final Function<T, V> f) {
        return (t, u) -> ImmutablePair.of(f.apply(t), u);
    }

    public static <T, U, V> BiFunction<T, U, Pair<T, V>> onlyRight(final Function<U, V> f) {
        return (t, u) -> ImmutablePair.of(t, f.apply(u));
    }

    public static <T, U, V, W> BiFunction<T, U, Pair<V, W>> toBi(final Function<Pair<T, U>, Pair<V, W>> f) {
        return (t, u) -> f.apply(ImmutablePair.of(t, u));
    }

    public static <T, U, V> BiFunction<T, U, V> toBiSingle(final Function<Pair<T, U>, V> f) {
        return (t, u) -> f.apply(ImmutablePair.of(t, u));
    }

    public static <T, U, V> Function<Pair<T, U>, V> fromBi(final BiFunction<T, U, V> f) {
        return p -> f.apply(p.getLeft(), p.getRight());
    }

    public static <T, U> Function<T, U> constant(final U u) {
        return ignored -> u;
    }

    public static <T, U> Function<List<T>, List<U>> mapWithIndex(final BiFunction<Integer, T, U> f) {
        return l -> mapWithIndex(f, l);
    }

    public static <T, U> List<U> mapWithIndex(final BiFunction<Integer, T, U> f, final List<T> ts) {
        final List<U> result = new ArrayList<>();

        for (int i = 0; i < ts.size(); i++) {
            result.add(f.apply(i, ts.get(i)));
        }

        return result;
    }

    public static <T, U> Function<List<T>, List<U>> map(final Function<T, U> f) {
        return l -> map(f, l);
    }

    public static <T, U> List<U> map(final Function<T, U> f, final List<T> ts) {
        return ts.stream().map(f).collect(Collectors.toList());
    }

    public static <T, U> Function<Set<T>, Set<U>> mapSet(final Function<T, U> f) {
        return l -> mapSet(f, l);
    }

    public static <T, U> Set<U> mapSet(final Function<T, U> f, final Set<T> ts) {
        return ts.stream().map(f).collect(Collectors.toSet());
    }

    // TODO: move to supplier utils
    public static Supplier<Void> asVoid(final Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

    // TODO: filter, reduce
    // TODO: Also copy functions for Maps
}
