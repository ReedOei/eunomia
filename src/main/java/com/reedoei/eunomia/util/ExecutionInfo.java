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

    private final List<String> javaOpts;
    private final Class<?> clz;

    public ExecutionInfo(final String classpath, @Nullable final Path javaAgent, final List<String> javaOpts, final Class<?> clz) {
        this.classpath = classpath;
        this.javaAgent = javaAgent;
        this.javaOpts = javaOpts;
        this.clz = clz;
    }

    public String classpath() {
        return classpath;
    }

    @Nullable
    public Path javaAgent() {
        return javaAgent;
    }

    public List<String> javaOpts() {
        return javaOpts;
    }

    public List<String> args(final String... args) {
        final List<String> allArgs = ListUtil.fromArray("java", "-cp", classpath());

        final Path javaAgent = javaAgent();
        if (javaAgent != null) {
            allArgs.add("-javaagent:" + javaAgent.toAbsolutePath().toString());
        }

        allArgs.addAll(javaOpts);

        allArgs.add(Objects.requireNonNull(clz.getCanonicalName()));
        allArgs.addAll(Arrays.asList(args));

        return allArgs;
    }
}
