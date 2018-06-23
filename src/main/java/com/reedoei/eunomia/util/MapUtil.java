package com.reedoei.eunomia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapUtil {
    // TODO: Make all methods return syncrhonized collections by default when creating a new collection.
    // TODO: Move map/filter/etc. into specific util packages.
    public static <K, V> Function<Map<K, V>, Map<K, V>> filter(final BiPredicate<K, V> pred) {
        return m -> filter(pred, m);
    }

    public static <K, V> Map<K, V> filter(final BiPredicate<K, V> pred, final Map<K, V> m) {
        final Map<K, V> result = Collections.synchronizedMap(new HashMap<>(m));

        m.forEach((k, v) -> {
            if (pred.test(k, v)) {
                result.put(k, v);
            }
        });

        return result;
    }

    public static Function<String, Map<String, String>> reader() {
        return reader(Function.identity(), Function.identity());
    }

    public static <K> Function<String, Map<K, String>> keyReader(final Function<String, K> keyReader) {
        return reader(keyReader, Function.identity());
    }

    public static <V> Function<String, Map<String, V>> valueReader(final Function<String, V> valueReader) {
        return reader(Function.identity(), valueReader);
    }

    public static <K, V> Function<String, Map<K, V>> reader(final Function<String, K> keyReader,
                                                            final Function<String, V> valueReader) {
        return s -> read(keyReader, valueReader, s);
    }

    public static <K> Map<K, String> keyRead(final Function<String, K> keyReader, final String s) {
        return read(keyReader, Function.identity(), s);
    }

    public static <V> Map<String, V> valueRead(final Function<String, V> valueReader, final String s) {
        return read(Function.identity(), valueReader, s);
    }

    public static <K, V> Map<K, V> read(final Function<String, K> keyReader,
                                        final Function<String, V> valueReader,
                                        final String s) {
        return Arrays.stream(s.substring(1, s.length() - 1).split(","))
                .map(String::trim)
                .collect(Collectors.toMap(kStr -> keyReader.apply(kStr.split("=")[0]),
                                          vStr -> valueReader.apply(vStr.split("=")[1]),
                                          (a, b) -> b));
    }

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
