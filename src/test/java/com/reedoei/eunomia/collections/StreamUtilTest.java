package com.reedoei.eunomia.collections;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
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

    @Test
    public void fromStream() {
        int total = 0;
        for (final Integer i : StreamUtil.fromStream(IntStream.range(0, 10).boxed())) {
            total += i;
        }

        assertEquals(45, total);
    }

//    @Test
//    public void testTakeWhile() {
//        final List<Integer> collect = StreamUtil.takeWhile(i -> i < 10, Stream.iterate(0, v -> v + 1)).collect(Collectors.toList());
//
//        assertEquals(ListUtil.fromArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), collect);
//    }
}