package com.reedoei.eunomia.collections;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class PairStreamTest {
    @Test
    public void product() {
        assertEquals(Arrays.asList(1, 2, 3, 2, 4, 6, 3, 6, 9),
                PairStream.product(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3))
                .mapToStream((a, b) -> a * b)
                .collect(Collectors.toList()));
    }
}