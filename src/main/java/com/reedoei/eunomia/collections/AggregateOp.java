package com.reedoei.eunomia.collections;

@FunctionalInterface
public interface AggregateOp<T> {
    T aggregate(final Aggregator<T> aggregator);
}
