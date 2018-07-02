package com.reedoei.eunomia.io;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.PrintStream;
import java.util.function.Supplier;

@Deprecated // In 1.3.0
public class CaptureErrStream<T> extends CaptureStream<T>  {
    public CaptureErrStream(final Supplier<T> supplier) {
        super(supplier);
    }

    @Override
    protected void setStream(final PrintStream stream) {
        System.setErr(stream);
    }

    @NonNull
    @Override
    protected PrintStream getStream() {
        return System.err;
    }
}
