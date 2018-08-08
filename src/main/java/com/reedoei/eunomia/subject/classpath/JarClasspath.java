package com.reedoei.eunomia.subject.classpath;

import com.reedoei.eunomia.util.OptionalCatcher;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.jar.JarFile;

public class JarClasspath implements ClasspathElement {
    private final Path path;
    private final Optional<JarFile> jarFile;

    public JarClasspath(final Path path) {
        this.path = path;

        this.jarFile = new OptionalCatcher<>(() -> new JarFile(path.toFile())).run();
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

        return jarFile.flatMap(j -> j.stream()
                .filter(entry -> entry.getName().equals(classOrPackageName))
                .findFirst()
                .map(entry -> Paths.get(entry.getName())));
    }
}
