package com.reedoei.eunomia.data.caching;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;

public abstract class Cache<T> {
    @Nullable
    protected T t = null;

    protected abstract @NonNull T generate();

    public T get() {
        if (t == null) {
            t = generate();
        }

        return t;
    }

    public T with(final Consumer<T> consumer) {
        consumer.accept(get());
        if (t == null) {
            throw new IllegalStateException("Value is null after calling get()!");
        }
        return t;
    }
}
