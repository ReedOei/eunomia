package com.reedoei.eunomia.functional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Cons {
    public static <T, U> BiConsumer<T, U> onlyLeft(final Consumer<T> consumer) {
        return (t, u) -> consumer.accept(t);
    }

    public static <T, U> BiConsumer<T, U> onlyRight(final Consumer<U> consumer) {
        return (t, u) -> consumer.accept(u);
    }

    public static <T, U> BiConsumer<T, U> toBi(final Consumer<Pair<T, U>> consumer) {
        return (t, u) -> consumer.accept(ImmutablePair.of(t, u));
    }

    public static <T, U> Consumer<Pair<T, U>> fromBi(final BiConsumer<T, U> consumer) {
        return p -> consumer.accept(p.getLeft(), p.getRight());
    }
}
