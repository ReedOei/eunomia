package com.reedoei.eunomia.collections;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class StreamUtilTest {
    @Test
    public void testRemoveEmpty() {
        assertTrue(StreamUtil.removeEmpty(IntStream.range(0, 100).boxed()
                .map(i -> {
                    if (i == null) {
                        return Optional.empty();
                    } else {
                       return i > 40 ? Optional.of(i) : Optional.empty();
                    }
                }))
                .allMatch(i -> i > 40));
    }

    @Test
    public void testSeq() {
        final List<Integer> l = new ArrayList<>();
        final Stream<Boolean> booleanStream = IntStream.range(0, 100).boxed().map(l::add);
        assertTrue(l.isEmpty());
        StreamUtil.seq(booleanStream);
        assertFalse(l.isEmpty());
    }
}