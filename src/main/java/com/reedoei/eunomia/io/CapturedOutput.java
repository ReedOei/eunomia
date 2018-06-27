package com.reedoei.eunomia.io;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

public class CapturedOutput<T> {
    private final @Nullable Throwable error;
    private final @Nullable T t;
    private final ByteArrayOutputStream outputStream;

    public CapturedOutput(final @Nullable Throwable error,
                          final @Nullable T t,
                          final ByteArrayOutputStream outputStream) {
        this.error = error;
        this.t = t;
        this.outputStream = outputStream;
    }

    public boolean success() {
        return error == null;
    }

    public boolean hadError() {
        return !success();
    }

    @Nullable
    public Throwable error() {
        return error;
    }

    @Nullable
    public T valueRequired() {
        Preconditions.checkState(success(), "Cannot get value from operation that had an error!");
        return t;
    }

    public Optional<T> value() {
        // TODO: Maybe contribute and improve nullness checker.
        // Doing this instead of Optional.ofNullable to satisfy NullnessChecker.
        if (t == null) {
            return Optional.empty();
        } else {
            return Optional.of(t);
        }
    }

    public ByteArrayOutputStream output() {
        return outputStream;
    }

    public String stringOutput() {
        return outputStream.toString();
    }

    @Override
    public String toString() {
        return stringOutput();
    }
}
