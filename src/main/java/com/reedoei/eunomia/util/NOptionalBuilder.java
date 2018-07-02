package com.reedoei.eunomia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Deprecated // In 1.3.0
public class NOptionalBuilder<K, V> {
    private final Map<@NonNull K, Optional<V>> values = new HashMap<>();

    public NOptionalBuilder() {

    }

    public NOptionalBuilder<K, V> add(final @NonNull K key, final Optional<V> value) {
        values.put(key, value);
        return this;
    }

    public NOptional<K, V> build() {
        final Map<K, V> values = new HashMap<>();

        boolean found = false;

        for (final @NonNull K k : this.values.keySet()) {
            if (this.values.get(k).isPresent()) {
                values.put(k, this.values.get(k).get());
            } else {
                found = true;
                break;
            }
        }

        return new NOptional<>(values, !found);
    }
}
