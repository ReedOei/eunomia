package com.reedoei.eunomia.functional;

public interface ThrowingPredicate<T> {
    boolean test(final T t) throws Exception;
}
