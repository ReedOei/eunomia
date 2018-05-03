package com.reedoei.eunomia.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NOptionalBuilder<K, V> {
    private final Map<K, Optional<V>> values = new HashMap<>();

    public NOptionalBuilder() {

    }

    public NOptionalBuilder<K, V> add(final K key, final Optional<V> value) {
        values.put(key, value);
        return this;
    }

    public NOptional<K, V> build() {
        final Map<K, V> values = new HashMap<>();

        boolean found = false;

        for (final K k : this.values.keySet()) {
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
