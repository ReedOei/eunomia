package com.reedoei.eunomia.io.files;

import com.reedoei.eunomia.collections.ListEx;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // List files and close the directory streams
    private static ListEx<Path> listFiles(final Path path) throws IOException {
        final ListEx<Path> result = new ListEx<>();

        try (final Stream<Path> stream = Files.list(path)) {
            result.addAll(stream.collect(Collectors.toList()));
        }

        return result;
    }

    /**
     * Read a file, returning an empty stream if it fails, and a Stream containing a single
     * String with the contents of the file if is succeeds.
     *
     * This is useful for things like Files.list(path).safeReadFile(FileUtil::fileContents), because
     * it won't throw but will also not return bad results.
     */
    public static Stream<String> safeReadFile(final Path path) {
        try {
            return Stream.of(readFile(path));
        } catch (IOException ignored) {}

        return Stream.empty();
    }

    public static String readFile(final Path path) throws IOException {
        return new String(Files.readAllBytes(path), Charset.defaultCharset());
    }

    /**
     * Copy files that exist in the input path but not the destPath.
     * Works recursively.
     */
    public static void copyFiles(final Path inputPath, final Path destPath) throws IOException {
        for (final Path path : Files.walk(inputPath).collect(Collectors.toList())) {
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
