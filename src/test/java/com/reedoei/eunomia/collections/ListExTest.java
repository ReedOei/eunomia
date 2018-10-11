package com.reedoei.eunomia.collections;

import com.google.common.math.IntMath;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ListExTest {
    @Test
    public void testPermutations() {
        // Warning, don't make this range too big!
        final int start = 0;
        final int end = 4;
        final ListEx<Integer> l = ListEx.range(start, end);

        final int len = IntMath.factorial(end - start);

        final ListEx<ListEx<Integer>> permutations = ListEx.collect(l.permutations());

        assertEquals(len, permutations.size());

        final Set<ListEx<Integer>> s = new HashSet<>();

        for (final ListEx<Integer> permutation : permutations) {
            assertEquals(permutation.size(), l.size());

            assertTrue(l.containsAll(permutation));

            assertFalse(s.contains(permutation));
            s.add(permutation);
        }
    }
}