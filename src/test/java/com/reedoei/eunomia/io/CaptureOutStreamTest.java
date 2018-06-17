package com.reedoei.eunomia.io;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class CaptureOutStreamTest {
    @Test
    public void testRun() {
        assertThat(new CaptureOutStream(() -> System.out.println("Hello World!")).run().toString(),
                equalTo("Hello World!" + System.lineSeparator()));
    }

    @Test
    public void testErrNotCaptured() {
        final String output = new CaptureOutStream(() -> {
            System.out.println("2 + 2 = 4");
            System.err.println("Testing");
        }).run().toString();

        assertThat(output, equalTo("2 + 2 = 4" + System.lineSeparator()));
    }
}