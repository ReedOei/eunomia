package com.reedoei.eunomia.util;

import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;

public class TestUtil {
    public static <T> void testThat(final Optional<T> opt, final Predicate<T> pred) {
        assertTrue(opt.isPresent());
        assertTrue(pred.test(opt.get()));
    }
}
