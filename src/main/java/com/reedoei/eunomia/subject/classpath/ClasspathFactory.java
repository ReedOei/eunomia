package com.reedoei.eunomia.subject.classpath;

import java.nio.file.Files;
import java.nio.file.Path;

public class ClasspathFactory {
    public static ClasspathElement forPath(final Path path) {
        if (Files.isDirectory(path)) {
            return new DirClasspath(path);
        } else if (path.toString().endsWith(".jar")) {
            return new JarClasspath(path);
        } else {
            throw new IllegalArgumentException("Error");
        }
    }
}
