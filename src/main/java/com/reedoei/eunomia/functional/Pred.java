package com.reedoei.eunomia.functional;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Predicate;

public class Pred {
    public static <T> Predicate<T> nullSafe(final Predicate<@NonNull T> pred) {
        return t -> t == null || pred.test(t);
    }

    public static <T> Predicate<T> negate(final Predicate<T> pred) {
        return pred.negate();
    }
}
