package com.reedoei.eunomia.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Averager<N extends Number> {
    private final List<N> values = new ArrayList<>();

    public Averager() {
    }

    public Averager(final Stream<N> values) {
        this(values.collect(Collectors.toList()));
    }

    public Averager(final Collection<N> values) {
        this.values.addAll(values);
    }

    public Averager add(final N value) {
        values.add(value);

        return this;
    }

    public Averager addAll(final Collection<N> values) {
        this.values.addAll(values);

        return this;
    }

    public double arithMean() {
        return mean();
    }

    public double mean() {
        return MathUtil.sum(values) / values.size();
    }

    public double average() {
        return mean();
    }

    public double geoMean() {
        return Math.pow(MathUtil.product(values), 1.0 / values.size());
    }

    public List<N> getValues() {
        return values;
    }
}
