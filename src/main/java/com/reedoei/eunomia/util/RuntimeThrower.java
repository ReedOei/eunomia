package com.reedoei.eunomia.util;

public class RuntimeThrower<T> {
    private final Computation<T> computation;

    public RuntimeThrower(final Computation<T> computation) {
        this.computation = computation;
    }

    public T run() {
        try {
            return computation.run();
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else {
                throw new RuntimeException(throwable);
            }
        }
    }
}
