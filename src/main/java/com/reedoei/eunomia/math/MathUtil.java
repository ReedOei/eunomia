package com.reedoei.eunomia.math;

import java.util.Collection;
import java.util.List;

public class MathUtil {
    public static double sum(final Collection<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    public static double product(final Collection<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue)
                .reduce(1.0, (a, b) -> a * b);
    }

    // TODO: Add binary operators for things like add, divide, etc.
}
