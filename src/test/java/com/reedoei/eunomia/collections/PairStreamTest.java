package com.reedoei.eunomia.collections;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class PairStreamTest {
    @Test
    public void product() {
        assertEquals(Arrays.asList(1, 2, 3, 2, 4, 6, 3, 6, 9),
                PairStream.product(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3))
                .mapToStream((a, b) -> a * b)
                .collect(Collectors.toList()));
    }

    @Test
    public void testFromStream() {
        final int sum = PairStream.fromStream(Stream.of(1, 2, 3, 4, 5), x -> x * 5, y -> y - 5)
                .mapToIntStream((x, y) -> x + y)
                .sum();

        assertEquals(65, sum);
    }
}