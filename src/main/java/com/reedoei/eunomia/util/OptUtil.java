package com.reedoei.eunomia.util;

import com.reedoei.eunomia.functional.TriConsumer;
import com.reedoei.eunomia.functional.TriFunction;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class OptUtil {
    public static <T, U> void ifAllPresent(final Optional<T> optT,
                                           final Optional<U> optU,
                                           final BiConsumer<T, U> consumer) {
        if (optT.isPresent() && optU.isPresent()) {
            consumer.accept(optT.get(), optU.get());
        }
    }

    public static <T,U,V> void ifAllPresent(final Optional<T> optT,
                                            final Optional<U> optU,
                                            final Optional<V> optV,
                                            final TriConsumer<T, U, V> consumer) {
        if (optT.isPresent() && optU.isPresent() && optV.isPresent()) {
            consumer.accept(optT.get(), optU.get(), optV.get());
        }
    }

    public static <T, U, V> Optional<V> map(final Optional<T> optT,
                                            final Optional<U> optU,
                                            final BiFunction<T, U, V> f) {
        if (optT.isPresent() && optU.isPresent()) {
            return Optional.ofNullable(f.apply(optT.get(), optU.get()));
        } else {
            return Optional.empty();
        }
    }

    public static <T, U, V, W> Optional<W> map(final Optional<T> optT,
                                               final Optional<U> optU,
                                               final Optional<V> optV,
                                               final TriFunction<T, U, V, W> f) {
        if (optT.isPresent() && optU.isPresent() && optV.isPresent()) {
            return Optional.ofNullable(f.apply(optT.get(), optU.get(), optV.get()));
        } else {
            return Optional.empty();
        }
    }
}
