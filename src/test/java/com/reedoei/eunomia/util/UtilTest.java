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

    @Test
    public void takeWhileInc() {
    }

    @Test
    public void beforeInc() {
    }

    @Test
    public void modify() {
    }

    @Test
    public void prependAll() {
    }

    @Test
    public void prependAll1() {
    }

    @Test
    public void appendAll() {
    }

    @Test
    public void appendAll1() {
    }

    @Test
    public void topHalf() {
    }

    @Test
    public void botHalf() {
    }

    @Test
    public void groupBy() {
    }

    @Test
    public void where() {
    }

    @Test
    public void elementToString() {
    }

    @Test
    public void total() {
    }

    @Test
    public void count() {
    }

    @Test
    public void count1() {
    }

    @Test
    public void incrementBy() {
    }

    @Test
    public void addSet() {
    }

    @Test
    public void wrapSet() {
    }

    @Test
    public void parseDirToSetMap() {
    }

    @Test
    public void parseDirToSet() {
    }

    @Test
    public void parseToSet() {
    }

    @Test
    public void parseDir() {
    }

    @Test
    public void parseFromFile() {
    }

    @Test
    public void parseFromDir() {
    }

    @Test
    public void distinctByKey() {
    }

    @Test
    public void maxKey() {
    }

    @Test
    public void add() {
    }

    @Test
    public void common() {
    }
}