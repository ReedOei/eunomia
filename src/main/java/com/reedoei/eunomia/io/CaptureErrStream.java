package com.reedoei.eunomia.io;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.PrintStream;

public class CaptureErrStream extends CaptureStream {
    public CaptureErrStream(Runnable runnable) {
        super(runnable);
    }

    @Override
    protected void setStream(PrintStream stream) {
        System.setErr(stream);
    }

    @NonNull
    @Override
    protected PrintStream getStream() {
        return System.err;
    }
}
