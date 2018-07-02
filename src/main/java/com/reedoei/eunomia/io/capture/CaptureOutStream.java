package com.reedoei.eunomia.io.capture;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.PrintStream;
import java.util.function.Supplier;

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
