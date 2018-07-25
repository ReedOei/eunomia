package com.reedoei.eunomia.collections;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtil {
    public static <T> Stream<T> removeEmpty(final Stream<Optional<T>> s) {
        return s.filter(Optional::isPresent).map(Optional::get);
    }

    /**
     * Force the evaluation of the stream (name inspired by Haskell).
     */
    public static <T> void seq(final Stream<T> stream) {
        stream.forEach(t -> {});
    }

    public static <T> Iterable<T> fromStream(final Stream<T> stream) {
        // Iterable is a functional interface, so because stream implements the right methods we can
        // do this.
        return stream::iterator;
    }
}
