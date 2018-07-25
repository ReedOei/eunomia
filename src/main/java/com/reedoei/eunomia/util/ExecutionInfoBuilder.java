package com.reedoei.eunomia.util;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExecutionInfoBuilder {
    private String classpath = System.getProperty("java.class.path");
    private @Nullable Path javaAgent = null;
    private List<String> javaOpts = new ArrayList<>();
    private final Class<?> clz;

    public ExecutionInfoBuilder(final Class<?> clz) {
        this.clz = clz;
    }

    public ExecutionInfoBuilder classpath(final String classpath) {
        this.classpath = classpath;
        return this;
    }

    public ExecutionInfoBuilder javaAgent(final Path javaAgent) {
        this.javaAgent = javaAgent;
        return this;
    }

    public ExecutionInfoBuilder javaOpts(final List<String> javaOpts) {
        this.javaOpts = javaOpts;
        return this;
    }

    public ExecutionInfoBuilder addJavaOpts(final List<String> javaOpts) {
        this.javaOpts.addAll(javaOpts);
        return this;
    }

    public ExecutionInfoBuilder javaOpt(final String opt) {
        javaOpts.add(opt);
        return this;
    }

    public ExecutionInfo build() {
        return new ExecutionInfo(classpath, javaAgent, javaOpts, clz);
    }
}