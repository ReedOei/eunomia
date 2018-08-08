package com.reedoei.eunomia.ast.instrumentation;

import com.reedoei.eunomia.collections.ListUtil;
import com.reedoei.eunomia.io.files.FileUtil;
import com.reedoei.eunomia.util.StandardMain;
import soot.Main;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.Transform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// TODO: Make it easy to call a custom instrumenter.
// TODO: Add instrument() static method that starts new process (because that's what Soot needs), see
//       dt fixing tools.
public class Instrumentation extends StandardMain {
    public Instrumentation(final String[] args) {
        super(args);
    }

    public static void main(final String[] args) {
        try {
            new Instrumentation(args).run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    @Override
    protected void run() throws Exception {
        final String sootCp = getArg("soot-cp", "cp", "classpath").orElse(System.getProperty("java.class.path"));
        final Path inputPath = Paths.get(getArgRequired("input-dir"));
        final Path outputPath = Paths.get(getArg("input-dir").orElse("sootOutput"));
        final String instrumenterClass = getArgRequired("instrumenter");

        final List<String> instrumenterArgs = ListUtil.read(getArg("args").orElse("[]"));

        final Instrumenter instrumenter =
                (Instrumenter) Class.forName(instrumenterClass)
                                    .getConstructor(List.class)
                                    .newInstance(instrumenterArgs);

        final Pack jtp = PackManager.v().getPack("jtp");
        jtp.add(new Transform("jtp.instrumenter", instrumenter));

        Scene.v().setSootClassPath(sootCp);

        Main.main(new String[] {"-allow-phantom-refs", "-pp", "-w", "-process-path", inputPath.toAbsolutePath().toString()});
        FileUtil.copyFiles(Paths.get("sootOutput"), outputPath);

        if (!outputPath.toAbsolutePath().equals(Paths.get("sootOutput").toAbsolutePath())) {
            Files.delete(Paths.get("sootOutput"));
        }

        FileUtil.copyFiles(inputPath, outputPath);

        jtp.remove("jtp.instrumenter");
    }
}
