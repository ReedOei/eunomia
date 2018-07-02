package com.reedoei.eunomia.io;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.PrintStream;
import java.util.function.Supplier;

@Deprecated // In 1.3.0
public class CaptureOutStream<T> extends CaptureStream<T> {
    public CaptureOutStream(final Supplier<T> supplier) {
        super(supplier);
    }

    @Override
    protected void setStream(final PrintStream stream) {
        System.setOut(stream);
    }

    @NonNull
    @Override
    protected PrintStream getStream() {
        return System.out;
    }
}
