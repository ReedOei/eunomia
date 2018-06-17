package com.reedoei.eunomia.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

public class Frequency<K, V> implements Iterable<K> {
    public static <K, V> Frequency<K, V> initWith(final Iterable<K> col, final Supplier<V> defaultValue) {
        final Map<K, V> m = new HashMap<>();

        col.forEach(k -> m.put(k, defaultValue.get()));

        return new Frequency<>(m);
    }

    public static <K, V> Frequency<K, V> empty() {
        return new Frequency<>(new HashMap<>());
    }

    private final Map<K, V> frequency;

    private Frequency(Map<K, V> frequency) {
        this.frequency = frequency;
    }

    public <T, U> Frequency<K, V> count(final T t,
                                        final Function<T, Iterable<U>> f,
                                        final BiPredicate<K, U> check,
                                        final BiFunction<K, V, V> update) {
        for (final K k : frequency.keySet()) {
            for (final U u : f.apply(t)) {
                if (check.test(k, u)) {
                    frequency.compute(k, update);
                }
            }
        }

        return this;
    }

    public <T, U> Frequency<K, V> countMany(final Iterable<T> ts,
                                            final Function<T, Iterable<U>> f,
                                            final BiPredicate<K, U> check,
                                            final BiFunction<K, V, V> update) {
        ts.forEach(t -> count(t, f, check, update));

        return this;
    }

    public Frequency<K, V> put(final K k, final V v) {
        frequency.put(k, v);
        return this;
    }

    public Optional<V> get(final K k) {
        if (frequency.containsKey(k)) {
            return Optional.ofNullable(frequency.get(k));
        } else {
            return Optional.empty();
        }
    }

    public <T> Frequency<K, T> map(final Function<V, T> f) {
        return map((k, v) -> f.apply(v));
    }

    public <T> Frequency<K, T> map(final BiFunction<K, V, T> f) {
        final Map<K, T> result = new HashMap<>();

        frequency.forEach((k, v) -> result.put(k, f.apply(k, v)));

        return new Frequency<>(result);
    }

    public Frequency<K, V> filter(final Predicate<V> f) {
        return filter((k, v) -> f.test(v));
    }

    public Frequency<K, V> filter(final BiPredicate<K, V> f) {
        final Map<K, V> result = new HashMap<>();

        frequency.forEach((k, v) -> {
            if (f.test(k, v)) {
                result.put(k, v);
            }
        });

        return new Frequency<>(result);
    }

    public void forEach(final BiConsumer<K, V> f) {
        frequency.forEach(f);
    }

    public Map<K, V> getMap() {
        return frequency;
    }

    @Override
    @NotNull
    public Iterator<K> iterator() {
        return frequency.keySet().iterator();
    }
}
