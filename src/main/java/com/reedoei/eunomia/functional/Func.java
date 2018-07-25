package com.reedoei.eunomia.functional;

import com.reedoei.eunomia.collections.ListUtil;
import com.reedoei.eunomia.collections.SetUtil;
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

    @Deprecated // In 1.3.1
    public static <T, U> Function<List<T>, List<U>> mapWithIndex(final BiFunction<Integer, T, U> f) {
        return ListUtil.mapWithIndex(f);
    }

    @Deprecated // In 1.3.1
    public static <T, U> List<U> mapWithIndex(final BiFunction<Integer, T, U> f, final List<T> ts) {
        return ListUtil.mapWithIndex(f, ts);
    }

    @Deprecated // In 1.3.1
    public static <T, U> Function<List<T>, List<U>> map(final Function<T, U> f) {
        return ListUtil.map(f);
    }

    @Deprecated // In 1.3.1
    public static <T, U> List<U> map(final Function<T, U> f, final List<T> ts) {
        return ListUtil.map(f, ts);
    }

    @Deprecated // In 1.3.1
    public static <T, U> Function<Set<T>, Set<U>> mapSet(final Function<T, U> f) {
        return SetUtil.map(f);
    }

    @Deprecated // In 1.3.1
    public static <T, U> Set<U> mapSet(final Function<T, U> f, final Set<T> ts) {
        return SetUtil.map(f, ts);
    }

    public static <T, U, V> Function<T, Function<U, V>> curry(final BiFunction<T, U, V> f) {
        return t -> u -> f.apply(t, u);
    }

    public static <T, U, V> BiFunction<T, U, V> uncurry(final Function<T, Function<U, V>> f) {
        return (t, u) -> f.apply(t).apply(u);
    }

    public static <T, U, V, W> Function<T, Function<U, Function<V, W>>> curry(final TriFunction<T, U, V, W> f) {
        return t -> u -> v -> f.apply(t, u, v);
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
