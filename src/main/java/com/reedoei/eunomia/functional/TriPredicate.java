package com.reedoei.eunomia.functional;

@FunctionalInterface
public interface TriPredicate<T, U, V> {
    boolean test(final T t, final U u, final V v);
}
