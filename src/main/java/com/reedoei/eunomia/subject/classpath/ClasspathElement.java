package com.reedoei.eunomia.subject.classpath;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.util.Optional;

public interface ClasspathElement {
    Path path();

    Optional<Path> pathTo(final @Nullable String classOrPackageName);

    default boolean contains(final Class<?> clz) {
        final String canonicalName = clz.getCanonicalName();
        return canonicalName != null && contains(canonicalName);
    }

    default boolean contains(final String classOrPackageName) {
        return pathTo(classOrPackageName).isPresent();
    }

    default Optional<Path> pathTo(final Class<?> clz) {
        return pathTo(clz.getCanonicalName());
    }
}
