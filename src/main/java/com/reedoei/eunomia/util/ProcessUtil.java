package com.reedoei.eunomia.util;

import java.io.IOException;

public class ProcessUtil {
    public static Process runClass(final Class<?> clz, final String... args) throws IOException {
        return runClass(new ExecutionInfo(System.getProperty("java.class.path")), clz, args);
    }

    public static Process runClass(final String classpath,
                                   final Class<?> clz,
                                   final String... args) throws IOException {
        return runClass(new ExecutionInfo(classpath), clz, args);
    }

    public static Process runClass(final ExecutionInfo info, final Class<?> clz, final String... args)
            throws IOException {
        return new ProcessBuilder(info.args(clz, args))
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start();
    }
}
