package com.reedoei.eunomia.subject.classpath;

import com.reedoei.eunomia.collections.ListUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Classpath {
    public static Classpath current() {
        return build(System.getProperty("java.class.path"));
    }

    public static Classpath from(final Classpath... classpaths) {
        return Arrays.stream(classpaths).reduce(new Classpath(), Classpath::combineWith);
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

        final ClasspathBuilder builder = new ClasspathBuilder();
        cpPaths.forEach(builder::element);
        return builder.build();
    }

    private final List<ClasspathElement> elements = new ArrayList<>();

    public Classpath() {
        this(new ArrayList<>());
    }

    public Classpath(final List<ClasspathElement> elements) {
        this.elements.addAll(elements);
    }

    public boolean contains(final Class<?> clz) {
        return elements.stream().anyMatch(element -> element.contains(clz));
    }

    public Classpath add(final Path path) {
        return add(ClasspathFactory.forPath(path));
    }

    public Classpath add(final ClasspathElement element) {
        if (elements.stream().noneMatch(p -> p.path().toAbsolutePath().equals(element.path().toAbsolutePath()))) {
            elements.add(element);
        }

        return this;
    }

    public Classpath combineWith(final Classpath other) {
        other.elements().forEach(this::add);

        return this;
    }

    public List<ClasspathElement> elements() {
        return elements;
    }

    @Override
    public String toString() {
        return String.join(File.pathSeparator, ListUtil.map(ClasspathElement::show, elements()));
    }
}
