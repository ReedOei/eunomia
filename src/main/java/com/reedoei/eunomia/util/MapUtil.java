package com.reedoei.eunomia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class MapUtil {
    public static <K, V> Function<@NonNull K, Stream<@NonNull V>> getSafe(final Map<K, V> m) {
        return k -> {
            final V v = m.get(k);

            if (v == null) {
                return Stream.empty();
            } else {
                return Stream.of(v);
            }
        };
    }
}
