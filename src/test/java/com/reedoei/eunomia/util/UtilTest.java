package com.reedoei.eunomia.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class UtilTest {
    @Test
    public void inRangeTrue() {
        assertTrue(Util.inRange(4, 0, 10));
    }

    @Test
    public void inRangeFalse() {
        assertFalse(Util.inRange(-11, -10, 100));
    }

    @Test
    public void inRangeInclusiveMin() {
        assertTrue(Util.inRange(5, 5, 7));
    }

    @Test
    public void inRangeExclusiveMax() {
        assertFalse(Util.inRange(7, 5, 7));
    }

    @Test
    public void getNext() {
        final List<String> data = Arrays.asList("value", "10", "after value");

        TestUtil.testThat(Util.getNext(data, "value"), v -> v.equals("10"));
    }

    @Test
    public void getPrevious() {
        final List<String> data = Arrays.asList("value", "10", "after value");

        TestUtil.testThat(Util.getPrevious(data, "after value"), v -> v.equals("10"));
    }

    @Test
    public void getOffset() {
        final List<String> data = Arrays.asList("value", "10", "after value");

        TestUtil.testThat(Util.getOffset(data, "value", 2), v -> v.equals("after value"));
    }

    @Test
    public void tryNextFirstIsNotEmpty() {
        final Optional<Integer> a = Optional.of(1);
        final Optional<Integer> b = Optional.of(10);

        TestUtil.testThat(Util.tryNext(a, b), v -> v == 1);
    }

    @Test
    public void tryNextSecondIsNotEmpty() {
        final Optional<Integer> a = Optional.empty();
        final Optional<Integer> b = Optional.of(10);

        TestUtil.testThat(Util.tryNext(a, b), v -> v == 10);
    }

    @Test
    public void tryNextBothEmpty() {
        final Optional<Integer> a = Optional.empty();
        final Optional<Integer> b = Optional.empty();

        assertFalse(Util.tryNext(a, b).isPresent());
    }
}