package com.reedoei.eunomia.subject;

import java.nio.file.Path;

// TODO: Add new methods like compile, test, gather dependencies, etc.
// TODO: Add subjects for other build systems (ant, gradle)
// TODO: Move to data package, deprecate this
public interface Subject {
    Path testClasses();
    Path classes();
    Path dependencies();

    Path mainSrc();
    Path testSrc();

    String classpath();

    String getName();
}
