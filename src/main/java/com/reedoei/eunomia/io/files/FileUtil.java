package com.reedoei.eunomia.io.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
    public static boolean makeDirectoryDestructive(final Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.deleteIfExists(path);
            Files.createDirectory(path);
        }

        return true;
    }
}
