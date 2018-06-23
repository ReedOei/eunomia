package com.reedoei.eunomia.collections;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtil {
    public static <T> Stream<T> removeEmpty(final Stream<Optional<T>> s) {
        return s.filter(Optional::isPresent).map(Optional::get);
    }
}
