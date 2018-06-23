package com.reedoei.eunomia.math;

import com.sun.prism.null3d.NULL3DPipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Averager<N extends Number> {
    private final Collection<N> values = new ArrayList<>();

    public Averager() {
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

    public double geoMean() {
        return Math.pow(MathUtil.product(values), 1.0 / values.size());
    }
}
