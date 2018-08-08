package com.reedoei.eunomia.ast.instrumentation;

import com.reedoei.eunomia.util.ProcessUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Transformation {
    private final List<String> args;
    private final Class<? super Instrumenter> clz;

    public Transformation(final Class<? super Instrumenter> clz) {
        this.clz = clz;
        this.args = new ArrayList<>();
    }

    public Transformation(final Class<? super Instrumenter> clz, final List<String> args) {
        this.clz = clz;
        this.args = args;
    }

    public int instrument(final String sootCp, final Path inputPath)
            throws IOException, InterruptedException {
        return instrument(sootCp, inputPath, Paths.get("sootOutput"));
    }

    public int instrument(final String sootCp, final Path inputPath, final Path outputPath)
            throws IOException, InterruptedException {
        final String canonicalName = clz.getCanonicalName();

        if (canonicalName != null) {
            final Process process = ProcessUtil.runClass(Instrumentation.class,
                    "--soot-cp", sootCp,
                    "--input-dir", inputPath.toAbsolutePath().toString(),
                    "--output-dir", outputPath.toAbsolutePath().toString(),
                    "--instrumenter", canonicalName,
                    "--args", args.toString());

            return process.waitFor();
        }

        return 1;
    }
}
