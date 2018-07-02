package com.reedoei.eunomia.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class SystemUtil {
    public static Optional<Path> findOnPath(final String name) {
        final String sysPath = System.getenv("PATH");

        if (sysPath != null) {
            return Arrays.stream(sysPath.split(File.pathSeparator))
                    .map(Paths::get)
                    .flatMap(path -> {
                        try {
                            if (Files.isDirectory(path)) {
                                return Files.list(path);
                            } else {
                                return Stream.of(path);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return Stream.empty();
                    })
                    .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().equals(name))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }
}
