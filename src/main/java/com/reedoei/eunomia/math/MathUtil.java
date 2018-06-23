package com.reedoei.eunomia.math;

import java.util.Collection;

public class MathUtil {
    public static <N extends Number> double sum(final Collection<N> values) {
        return values.stream().mapToDouble(Number::doubleValue).sum();
    }

    public static <N extends Number> double product(final Collection<N> values) {
        return values.stream().mapToDouble(Number::doubleValue)
                .reduce(1.0, (a, b) -> a * b);
    }

    // TODO: Add binary operators for things like add, divide, etc.
}
