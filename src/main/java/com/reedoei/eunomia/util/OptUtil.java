package com.reedoei.eunomia.util;

import java.util.Optional;
import java.util.function.BiConsumer;

public class OptUtil {
    public static <T, U> void ifAllPresent(final Optional<T> optT,
                                           final Optional<U> optU,
                                           final BiConsumer<T, U> consumer) {
        if (optT.isPresent() && optU.isPresent()) {
            consumer.accept(optT.get(), optU.get());
        }
    }
}
