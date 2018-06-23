package com.reedoei.eunomia.data;

import com.reedoei.eunomia.collections.MapUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class Frequency<K> implements Iterable<K> {
    public static <K> Frequency<K> initWith(final Iterable<K> keys) {
        final Map<K, Integer> m = new HashMap<>();

        keys.forEach(k -> m.put(k, 0));

        return new Frequency<>(m);
    }

    public static <K> Frequency<K> empty() {
        return new Frequency<>(new HashMap<>());
    }

    private final Map<K, Integer> frequency;

    private Frequency(Map<K, Integer> frequency) {
        this.frequency = frequency;
    }

    public Frequency<K> count(final K key) {
        frequency.compute(key, MapUtil.incrementBy(1));

        return this;
    }

    public <T> Frequency<K> count(final T t,
                                  final BiPredicate<K, T> check) {
        return count(t, check, MapUtil.incrementBy(1));
    }

    public <T> Frequency<K> count(final T t,
                                  final BiPredicate<K, T> check,
                                  final BiFunction<K, Integer, Integer> update) {
        for (final K k : frequency.keySet()) {
            if (check.test(k, t)) {
                frequency.compute(k, update);
            }
        }

        return this;
    }

    public Frequency<K> put(final K k, final Integer v) {
        frequency.put(k, v);
        return this;
    }

    public Optional<Integer> get(final @NonNull K k) {
        if (frequency.containsKey(k)) {
            return Optional.ofNullable(frequency.get(k));
        } else {
            return Optional.empty();
        }
    }

    public Frequency<K> map(final Function<Integer, Integer> f) {
        return map((k, v) -> f.apply(v));
    }

    public Frequency<K> map(final BiFunction<K, Integer, Integer> f) {
        final Map<K, Integer> result = new HashMap<>();

        frequency.forEach((k, v) -> result.put(k, f.apply(k, v)));

        return new Frequency<>(result);
    }

    public Frequency<K> filter(final Predicate<Integer> f) {
        return filter((k, v) -> f.test(v));
    }

    public Frequency<K> filter(final BiPredicate<K, Integer> f) {
        final Map<K, Integer> result = new HashMap<>();

        frequency.forEach((k, v) -> {
            if (f.test(k, v)) {
                result.put(k, v);
            }
        });

        return new Frequency<>(result);
    }

    public void forEach(final BiConsumer<K, Integer> f) {
        frequency.forEach(f);
    }

    public Optional<K> max() {
        return MapUtil.maxKey(frequency);
    }

    public Optional<K> min() {
        return MapUtil.minKey(frequency);
    }

    public Map<K, Integer> getMap() {
        return frequency;
    }

    @Override
    @NonNull
    public Iterator<K> iterator() {
        return frequency.keySet().iterator();
    }

    public int size() {
        return frequency.size();
    }
}
