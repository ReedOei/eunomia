package com.reedoei.eunomia.collections;

import java.util.ArrayList;

public class Aggregator<T> extends ArrayList<T> {
    private final AggregateOp<T> op;

    public Aggregator(final AggregateOp<T> op) {
        this.op = op;
    }

    public T get() {
        return op.aggregate(this);
    }
}
