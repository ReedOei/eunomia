package com.reedoei.eunomia.functional;

public interface ThrowingFunction<T, U> {
    public U apply(final T t) throws Exception;
}
