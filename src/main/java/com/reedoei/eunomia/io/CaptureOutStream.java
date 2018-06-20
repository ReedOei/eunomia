package com.reedoei.eunomia.io;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.PrintStream;

public class CaptureOutStream extends CaptureStream {
    public CaptureOutStream(Runnable runnable) {
        super(runnable);
    }

    @Override
    protected void setStream(PrintStream stream) {
        System.setOut(stream);
    }

    @NonNull
    @Override
    protected PrintStream getStream() {
        return System.out;
    }
}
