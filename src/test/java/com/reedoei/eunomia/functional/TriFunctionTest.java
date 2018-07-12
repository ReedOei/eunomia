package com.reedoei.eunomia.functional;

import org.junit.Test;

import static org.junit.Assert.*;

public class TriFunctionTest {
    @Test
    public void apply() {
        final TriFunction<Integer, Integer, Integer, Integer> f = (a, b, c) -> a + b * c;

        assertEquals(-11, f.apply(5, 2, -8).intValue());
    }
}