package com.reedoei.eunomia.util;

import com.reedoei.eunomia.collections.ListUtil;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExecutionInfo {
    private final String classpath;

    @Nullable
    private final Path javaAgent;

    public ExecutionInfo() {
        this(System.getProperty("java.class.path"));
    }

    public ExecutionInfo(final String classpath) {
        this(classpath, null);
    }

    public ExecutionInfo(@Nullable final Path javaAgent) {
        this(System.getProperty("java.class.path"), javaAgent);
    }

    public ExecutionInfo(final String classpath, @Nullable final Path javaAgent) {
        this.classpath = classpath;
        this.javaAgent = javaAgent;
    }

    public String classpath() {
        return classpath;
    }

    @Nullable
    public Path javaAgent() {
        return javaAgent;
    }

    public List<String> args(final Class<?> clz, final String... args) {
        final List<String> allArgs = ListUtil.fromArray("java", "-cp", classpath());

        final Path javaAgent = javaAgent();
        if (javaAgent != null) {
            allArgs.add("-javaagent:" + javaAgent.toAbsolutePath().toString());
        }

        allArgs.add(Objects.requireNonNull(clz.getCanonicalName()));
        allArgs.addAll(Arrays.asList(args));

        return allArgs;
    }
}
