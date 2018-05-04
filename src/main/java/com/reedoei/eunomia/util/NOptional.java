package com.reedoei.eunomia.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class NOptional<K, V> {
    private final Map<K, V> values = new HashMap<>();
    private final boolean allPresent;

    NOptional(final Map<K, V> values, final boolean allPresent) {
        this.values.putAll(values);
        this.allPresent = allPresent;
    }

    public <B> NOptional<K, B> fmap(final Function<V, B> f) {
        if (allPresent) {
            final Map<K, B> newValues = new HashMap<>();

            for (final K k : values.keySet()) {
                newValues.put(k, f.apply(values.get(k)));
            }

            return new NOptional<>(newValues, true);
        } else {
            return new NOptional<>(new HashMap<>(), false);
        }
    }

    public <B> NOptional<K, B> bind(final Function<V, Optional<B>> f) {
        if (allPresent) {
            final NOptionalBuilder<K, B> builder = new NOptionalBuilder<>();

            for (final K k : values.keySet()) {
                builder.add(k, f.apply(values.get(k)));
            }

            return builder.build();
        } else {
            return new NOptional<>(new HashMap<>(), false);
        }
    }

    public void ifPresent(final Consumer<Map<K,V>> f) {
        if (allPresent) {
            f.accept(values);
        }
    }

    public <T> T fromOptional(final T def, final Function<Map<K,V>, T> f) {
        if (allPresent) {
            return f.apply(values);
        } else {
            return def;
        }
    }
}
