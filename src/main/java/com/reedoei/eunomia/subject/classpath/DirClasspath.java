package com.reedoei.eunomia.subject.classpath;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class DirClasspath implements ClasspathElement {
    private final Path path;

    public DirClasspath(final Path path) {
        this.path = path;
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public Optional<Path> pathTo(final @Nullable String classOrPackageName) {
        if (classOrPackageName == null) {
            return Optional.empty();
        }

        final Path path = Paths.get("", classOrPackageName.split("."));
        final Path pathToClass = Paths.get("", (classOrPackageName + ".class").split("."));

        if (Files.exists(path)) {
            return Optional.of(path);
        } else if (Files.exists(pathToClass)) {
            return Optional.of(pathToClass);
        } else {
            return Optional.empty();
        }
    }
}
