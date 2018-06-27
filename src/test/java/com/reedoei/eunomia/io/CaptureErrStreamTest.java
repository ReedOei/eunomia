package com.reedoei.eunomia.io;

import com.reedoei.eunomia.functional.Func;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class CaptureErrStreamTest {
    @Test
    public void testRun() {
        assertThat(new CaptureErrStream<>(Func.asVoid(() -> System.err.println("Hello World!"))).run().stringOutput(),
                equalTo("Hello World!" + System.lineSeparator()));
    }

    @Test
    public void testOutNotCaptured() {
        final String output = new CaptureErrStream<>(Func.asVoid(() -> {
            System.out.println("2 + 2 = 4");
            System.err.println("Testing");
        })).run().toString();

        assertThat(output, equalTo("Testing" + System.lineSeparator()));
    }
}