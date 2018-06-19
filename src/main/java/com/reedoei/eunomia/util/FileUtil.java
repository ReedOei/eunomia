package com.reedoei.eunomia.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
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
}
