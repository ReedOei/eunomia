package com.reedoei.eunomia.functional;

@FunctionalInterface
public interface TriFunction<T, U, V, W> {
    W apply(final T t, final U u, final V v);
}
