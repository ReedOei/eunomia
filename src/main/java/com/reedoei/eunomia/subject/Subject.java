package com.reedoei.eunomia.subject;

import com.reedoei.eunomia.ast.JavaProject;
import com.reedoei.eunomia.collections.ListUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

// TODO: Add new methods like compile, test, gather dependencies, etc.
// TODO: Add subjects for other build systems (ant, gradle)
// TODO: Move to data package, deprecate this
public interface Subject {
    Path root();

    Path testClasses();
    Path classes();
    Path dependencies();

    Path mainSrc();
    Path testSrc();

    String classpath();

    default String combineClasspath(final Subject other) {
        final List<@NonNull String> ourPaths = ListUtil.fromArray(classpath().split(File.pathSeparator));
        final List<@NonNull String> otherPaths = ListUtil.fromArray(other.classpath().split(File.pathSeparator));

        for (final String path : otherPaths) {
            if (!ourPaths.contains(path)) {
                ourPaths.add(path);
            }
        }

        return String.join(File.pathSeparator, ourPaths);
    }

    String getName();

    Stream<Subject> submodules();

    JavaProject project() throws Exception;
}
