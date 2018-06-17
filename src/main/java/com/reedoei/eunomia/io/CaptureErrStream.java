package com.reedoei.eunomia.io;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class CaptureErrStream extends CaptureStream {
    public CaptureErrStream(Runnable runnable) {
        super(runnable);
    }

    @Override
    protected void setStream(PrintStream stream) {
        System.setErr(stream);
    }

    @NotNull
    @Override
    protected PrintStream getStream() {
        return System.err;
    }
}
