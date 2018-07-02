package com.reedoei.eunomia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Deprecated // In 1.3.0
public class NOptional<K, V> {
    private final Map<@NonNull K, @NonNull V> values = new HashMap<>();
    private final boolean allPresent;

    NOptional(final Map<K, V> values, final boolean allPresent) {
        this.values.putAll(values);
        this.allPresent = allPresent;
    }

    public <B> NOptional<K, B> fmap(final Function<V, B> f) {
        if (allPresent) {
            final Map<K, B> newValues = new HashMap<>();

            values.forEach((k, v) -> newValues.put(k, f.apply(v)));

            return new NOptional<>(newValues, true);
        } else {
            return new NOptional<>(new HashMap<>(), false);
        }
    }

    public <B> NOptional<K, B> bind(final Function<V, Optional<B>> f) {
        if (allPresent) {
            final NOptionalBuilder<K, B> builder = new NOptionalBuilder<>();

            values.forEach((k, v) -> builder.add(k, f.apply(v)));

            return builder.build();
        } else {
            return new NOptional<>(new HashMap<>(), false);
        }
    }

    public void ifPresent(final Consumer<Map<@NonNull K, @NonNull V>> f) {
        if (allPresent) {
            f.accept(values);
        }
    }

    public <T> T fromOptional(final T def, final Function<Map<@NonNull K, @NonNull V>, T> f) {
        if (allPresent) {
            return f.apply(values);
        } else {
            return def;
        }
    }
}
