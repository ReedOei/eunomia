package com.reedoei.eunomia.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Averager {
    private final List<Double> values = new ArrayList<>();

    public Averager() {

    }

    public Averager(final List<Double> values) {
        this.values.addAll(values);
    }

    public Averager add(final double value) {
        values.add(value);

        return this;
    }

    public Averager addAll(final Collection<Double> values) {
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
