package com.reedoei.eunomia.io;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

public abstract class CaptureStream<T> {
    private final Supplier<T> supplier;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private boolean hasRun = false;

    public CaptureStream(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public CapturedOutput<T> run() {
        if (hasRun) {
            throw new IllegalStateException("Can only execute a CaptureStream once! To repeat a computation, create a new CaptureStream!");
        }

        @Nullable T value = null;
        final PrintStream stream = getStream();
        @Nullable Throwable error = null;

        try {
            setStream(new PrintStream(outputStream));

            value = supplier.get();
        } catch (Throwable t) {
            error = t;
        } finally {
            setStream(stream);
            hasRun = true;
        }

        return new CapturedOutput<>(error, value, outputStream);
    }

    protected abstract void setStream(final PrintStream stream);

    @NonNull
    protected abstract PrintStream getStream();

}
