package com.reedoei.eunomia.util;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtil {
    public static <T> Stream<T> removeEmpty(final Stream<Optional<T>> s) {
        return s.filter(Optional::isPresent).map(Optional::get);
    }
}
