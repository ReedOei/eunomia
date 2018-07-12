package com.reedoei.eunomia.subject;

import com.reedoei.eunomia.util.Util;

import java.nio.file.Path;

// TODO: Use maven calls to get the actual directories to handle project specific cases.
public class MavenSubject implements Subject {
    private final String name;
    private final Path root;

    public static MavenSubject fromRoot(final String name, final Path root) {
        return new MavenSubject(name, root);
    }

    public static MavenSubject fromTarget(final String name, final Path target) {
        return new MavenSubject(name, target.getParent());
    }

    private MavenSubject(final String name, final Path root) {
        this.name = name;
        this.root = root;
    }

    @Override
    public Path testClasses() {
        return root.resolve("target").resolve("test-classes");
    }

    @Override
    public Path classes() {
        return root.resolve("target").resolve("classes");
    }

    @Override
    public Path dependencies() {
        return root.resolve("target").resolve("dependency");
    }

    @Override
    public Path mainSrc() {
        return root.resolve("src").resolve("main").resolve("java");
    }

    @Override
    public Path testSrc() {
        return root.resolve("src").resolve("test").resolve("java");
    }

    @Override
    public String classpath() {
        return Util.buildClassPath(classes().toString(), testClasses().toString(), dependencies() + "/*");
    }

    @Override
    public String getName() {
        return name;
    }
}
