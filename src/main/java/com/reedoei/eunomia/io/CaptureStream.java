package com.reedoei.eunomia.io;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class CaptureStream {
    private final Runnable runnable;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private boolean hasRun = false;

    public CaptureStream(final Runnable runnable) {
        this.runnable = runnable;
    }

    public ByteArrayOutputStream run() {
        if (hasRun) {
            throw new IllegalStateException("Can only execute a CaptureStream once! To repeat a computation, create a new CaptureStream!");
        }

        try {
            final PrintStream stream = getStream();
            setStream(new PrintStream(outputStream));

            runnable.run();

            setStream(stream);
        } finally {
            hasRun = true;
        }

        return outputStream;
    }

    protected abstract void setStream(final PrintStream stream);

    @NotNull
    protected abstract PrintStream getStream();

}
