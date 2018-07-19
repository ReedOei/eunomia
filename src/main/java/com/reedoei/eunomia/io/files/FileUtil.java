package com.reedoei.eunomia.io.files;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileUtil {
    public static boolean makeDirectoryDestructive(final Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.deleteIfExists(path);
            Files.createDirectories(path);
        }

        return true;
    }

    public static boolean inParent(final String path, final String parent) {
        Path p = Paths.get(path);
        boolean found = false;

        for (int i = 0; i < p.getNameCount(); i++) {
            if (p.getName(i).toString().equals(parent)) {
                found = true;
                break;
            }
        }

        return found;
    }

    public static String readFile(final Path path) throws IOException {
        return new String(Files.readAllBytes(path), Charset.defaultCharset());
    }

    /**
     * Copy files that exist in the input path but not the destPath.
     * Works recursively.
     */
    public static void copyFiles(final Path inputPath, final Path destPath) throws IOException {
        for (Path path : Files.walk(inputPath).collect(Collectors.toList())) {
            final Path destFile = destPath.resolve(inputPath.relativize(path));

            if (!Files.exists(destFile)) {
                Files.copy(path, destFile);
            }
        }
    }

    public static boolean isEmpty(final Path path) {
        if (!Files.exists(path) || Files.isRegularFile(path)) {
            return true;
        }

        try {
            return Files.walk(path).count() == 0;
        } catch (IOException ignored) {}

        return true;
    }
}
