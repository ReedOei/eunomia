package com.reedoei.eunomia.subject.classpath;

import com.reedoei.eunomia.collections.ListUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Classpath {
    public static Classpath current() {
        return build(System.getProperty("java.class.path"));
    }

    public static Classpath build(final String path) {
        return build(path.split(System.getProperty("path.separator")));
    }

    public static Classpath build(final String... paths) {
        final List<Path> cpPaths = new ArrayList<>();

        for (String path : paths) {
            if (path.endsWith("*")) {
                path = path.substring(0, path.length() - 1);
                final File pathFile = new File(path);

                final File[] files = pathFile.listFiles();

                if (files != null) {
                    for (final File file : files) {
                        if (file.isFile() && file.getName().endsWith(".jar")) {
                            cpPaths.add(file.toPath().toAbsolutePath());
                        }
                    }
                }
            } else {
                cpPaths.add(Paths.get(path).toAbsolutePath());
            }
        }

        return new Classpath(cpPaths);
    }

    private final List<ClasspathElement> paths = new ArrayList<>();

    public Classpath(final List<Path> paths) {
        paths.stream().map(ClasspathFactory::forPath).forEach(this.paths::add);
    }

    public boolean contains(final Class<?> clz) {
        return paths.stream().anyMatch(element -> element.contains(clz));
    }

    public List<ClasspathElement> paths() {
        return paths;
    }

    @Override
    public String toString() {
        return String.join(File.pathSeparator, ListUtil.map(ClasspathElement::toString, paths()));
    }
}
