package com.reedoei.eunomia.util;

import com.reedoei.eunomia.collections.ListUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ProcessUtil {
    public static Process runClass(final String classpath,
                                   final Class<?> clz,
                                   final String... args) throws IOException {
        final List<String> allArgs = ListUtil.fromArray("java", "-cp", classpath, clz.getCanonicalName());
        allArgs.addAll(Arrays.asList(args));

        return new ProcessBuilder(allArgs)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start();
    }

    public static Process runClass(final Class<?> clz, final String... args) throws IOException {
        return runClass(System.getProperty("java.class.path"), clz, args);
    }
}
