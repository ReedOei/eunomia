package com.reedoei.eunomia.util;

@FunctionalInterface
public interface Computation<T> {
    T run() throws Throwable;
}
