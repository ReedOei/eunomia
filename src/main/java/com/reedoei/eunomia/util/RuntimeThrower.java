package com.reedoei.eunomia.util;

import java.util.concurrent.Callable;

public class RuntimeThrower<T> {
    private final Callable<T> computation;

    public RuntimeThrower(final Callable<T> computation) {
        this.computation = computation;
    }

    public T run() {
        try {
            return computation.call();
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else {
                throw new RuntimeException(throwable);
            }
        }
    }
}
