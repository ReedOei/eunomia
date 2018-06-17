package com.reedoei.eunomia.functional;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TriConsumerTest {
    @Test
    public void testAccept() throws Exception {
        final List<Integer> list = new ArrayList<>();

        final TriConsumer<Integer, Integer, Integer> f = (a, b, c) -> list.add(a + b + c);
        f.accept(1, 2, 3);

        assertEquals(Collections.singletonList(6), list);
    }
}