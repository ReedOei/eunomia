package com.reedoei.eunomia.io;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class CaptureErrStreamTest {
    @Test
    public void testRun() {
        assertThat(new CaptureErrStream(() -> System.err.println("Hello World!")).run().toString(),
                equalTo("Hello World!" + System.lineSeparator()));
    }

    @Test
    public void testOutNotCaptured() {
        final String output = new CaptureErrStream(() -> {
            System.out.println("2 + 2 = 4");
            System.err.println("Testing");
        }).run().toString();

        assertThat(output, equalTo("Testing" + System.lineSeparator()));
    }
}