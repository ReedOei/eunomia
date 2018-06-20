package com.reedoei.eunomia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListUtil {
    @NonNull
    public static Function<String, List<String>> reader() {
        return reader(Function.identity());
    }

    @NonNull
    public static <T> Function<String, List<T>> reader(@NonNull final Function<String, T> constructor) {
        return s -> read(s, constructor);
    }

    @NonNull
    public static <T> List<T> read(@NonNull final String s,
                                   @NonNull final Function<String, T> constructor) {
        return Arrays.stream(s
                .replace("[", "")
                .replace("]", "")
                .split(","))
                .map(String::trim)
                .map(constructor)
                .collect(Collectors.toList());
    }
}
