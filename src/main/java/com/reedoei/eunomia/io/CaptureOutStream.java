package com.reedoei.eunomia.io;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class CaptureOutStream extends CaptureStream {
    public CaptureOutStream(Runnable runnable) {
        super(runnable);
    }

    @Override
    protected void setStream(PrintStream stream) {
        System.setOut(stream);
    }

    @NotNull
    @Override
    protected PrintStream getStream() {
        return System.out;
    }
}
