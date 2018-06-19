package com.reedoei.eunomia.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListUtil {
    @NotNull
    public static <T> Function<String, List<T>> reader(@NotNull final Function<String, T> constructor) {
        return s -> read(s, constructor);
    }

    @NotNull
    public static <T> List<T> read(@NotNull final String s,
                                   @NotNull final Function<String, T> constructor) {
        return Arrays.stream(s
                .replace("[", "")
                .replace("]", "")
                .split(","))
                .map(String::trim)
                .map(constructor)
                .collect(Collectors.toList());
    }
}
