package com.reedoei.eunomia.subject.classpath;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClasspathBuilder {
    private final List<ClasspathElement> elements = new ArrayList<>();

    public ClasspathBuilder() {
    }

    public ClasspathBuilder element(final Path path) {
        elements.add(ClasspathFactory.forPath(path));

        return this;
    }

    public ClasspathBuilder element(final ClasspathElement element) {
        elements.add(element);

        return this;
    }

    public Classpath build() {
        return new Classpath(elements);
    }
}
