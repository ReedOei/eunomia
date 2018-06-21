package com.reedoei.eunomia.functional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Pred {
    public static <T> Predicate<T> nullSafe(final Predicate<@NonNull T> pred) {
        return t -> t == null || pred.test(t);
    }

    public static <T> Predicate<T> negate(final Predicate<T> pred) {
        return pred.negate();
    }

    public static <T, U> BiPredicate<T, U> onlyLeft(final Predicate<T> pred) {
        return (t, u) -> pred.test(t);
    }

    public static <T, U> BiPredicate<T, U> onlyRight(final Predicate<U> pred) {
        return (t, u) -> pred.test(u);
    }

    public static <T, U> BiPredicate<T, U> toBi(final Predicate<Pair<T, U>> pred) {
        return (t, u) -> pred.test(ImmutablePair.of(t, u));
    }

    public static <T, U> Predicate<Pair<T, U>> fromBi(final BiPredicate<T, U> pred) {
        return p -> pred.test(p.getLeft(), p.getRight());
    }
}
