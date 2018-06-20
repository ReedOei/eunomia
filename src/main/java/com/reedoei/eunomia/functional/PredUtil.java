package com.reedoei.eunomia.functional;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Predicate;

public class PredUtil {
    public static <T> Predicate<T> nullSafe(final Predicate<@NonNull T> pred) {
        return t -> t == null || pred.test(t);
    }
}
