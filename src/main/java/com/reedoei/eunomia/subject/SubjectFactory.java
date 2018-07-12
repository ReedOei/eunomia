package com.reedoei.eunomia.subject;

import java.nio.file.Files;
import java.nio.file.Path;

public class SubjectFactory {
    public static Subject forPath(final Path root) {
        // TODO: Replace with call to relevant build system
        final String name = root.toAbsolutePath().getFileName() != null ? root.toAbsolutePath().getFileName().toString() : "unknown";

        if (Files.exists(root.resolve("pom.xml"))) {
            return MavenSubject.fromRoot(name, root);
        } else {
            if (root.getFileName() != null && root.getFileName().toString().equals("target")) {
                if (root.getParent() != null) {
                    return forPath(root.getParent());
                }
            }

            throw new IllegalArgumentException("Subject in path: " + root + " uses unknown build system.");
        }
    }
}
