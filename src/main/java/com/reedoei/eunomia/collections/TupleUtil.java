package com.reedoei.eunomia.collections;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public class TupleUtil {
    public static <T,U> Function<Pair<T, T>, Pair<U, U>> fmap(final Function<T, U> f) {
        return p -> ImmutablePair.of(f.apply(p.getLeft()), f.apply(p.getRight()));
    }
}
