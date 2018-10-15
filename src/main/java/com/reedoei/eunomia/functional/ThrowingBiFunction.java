package com.reedoei.eunomia.functional;

public interface ThrowingBiFunction<T, U, V> {
    V apply(final T t, final U u) throws Exception;
}
