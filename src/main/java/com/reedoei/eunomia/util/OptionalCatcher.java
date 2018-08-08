package com.reedoei.eunomia.util;

import java.util.Optional;
import java.util.concurrent.Callable;

public class OptionalCatcher<T> {
    private final Callable<T> callable;

    public OptionalCatcher(final Callable<T> callable) {
        this.callable = callable;
    }

    public Optional<T> run() {
        try {
            return Optional.ofNullable(callable.call());
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
