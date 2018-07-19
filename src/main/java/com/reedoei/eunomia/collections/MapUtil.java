package com.reedoei.eunomia.collections;

import com.reedoei.eunomia.functional.TriFunction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapUtil {
    public static <K, U, V, W> Map<K, W> zip(final TriFunction<K, U, V, Optional<W>> f, final Map<K, U> a, final Map<K, V> b) {
        final Map<K, W> m = Collections.synchronizedMap(new HashMap<>());

        for (final K k : a.keySet()) {
            if (k != null && b.containsKey(k)) {
                f.apply(k, a.get(k), b.get(k)).ifPresent(w -> m.put(k, w));
            }
        }

        return m;
    }

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

    public static <K,V> Optional<Map.Entry<K, V>> where(final Map<K, V> map, BiPredicate<K, V> pred) {
        return map.entrySet().stream()
                .filter(entry -> pred.test(entry.getKey(), entry.getValue()))
                .findAny();
    }

    public static int total(final Map<String, Integer> map) {
        return map.values().stream().mapToInt(Integer::intValue).sum();
    }

    public static int count(final Map<String, Boolean> map) {
        return count(map, (k, v) -> v);
    }

    public static <K, V> int count(final Map<K, V> map, final BiPredicate<K, V> pred) {
        return Math.toIntExact(map.entrySet().stream()
                .filter(entry -> pred.test(entry.getKey(), entry.getValue()))
                .count());
    }

    public static <T> BiFunction<T, Integer, Integer> incrementBy(final int amount) {
        return (ignored, count) -> count == null ? amount : count + amount;
    }

    public static <K, V extends Comparable<? super V>> Optional<K> maxKey(final Map<K, V> map) {
        return map.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    public static <K, V extends Comparable<? super V>> Optional<K> minKey(final Map<K, V> map) {
        return map.entrySet().stream()
                .min(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    public static <K, V> Set<K> diff(final Map<K, V> a, final Map<K, V> b) {
        return diffBy(a, b, (aVal, bVal) -> aVal != null && bVal != null && !aVal.equals(bVal));
    }

    public static <K, V> Set<K> diffBy(final Map<K, V> a, final Map<K, V> b, final BiPredicate<V, V> pred) {
        final Set<K> result = Collections.synchronizedSet(new HashSet<>());

        a.forEach((k, v) -> {
            if (k != null && v != null && b.containsKey(k) && pred.test(v, b.get(k))) {
                result.add(k);
            }
        });

        return result;
    }
}
