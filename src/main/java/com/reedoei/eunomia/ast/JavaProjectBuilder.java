package com.reedoei.eunomia.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.reedoei.eunomia.io.files.FileUtil;
import com.reedoei.eunomia.string.searching.Searcher;
import com.reedoei.eunomia.string.searching.StringSearch;
import org.apache.commons.io.FileUtils;
import org.apache.maven.cli.MavenCli;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class JavaProjectBuilder {
    private Path repoPath;
    private Path path;
    private String ref;

    public JavaProjectBuilder(final Path repoPath) {
        this.repoPath = repoPath;
        this.path = repoPath.getParent();
        this.ref = "HEAD";
    }

    public JavaProjectBuilder repoPath(final Path repoPath) {
        this.repoPath = repoPath;
        this.path = repoPath.toAbsolutePath().getParent();

        return this;
    }

    public JavaProjectBuilder ref(final String ref) {
        this.ref = ref;
        return this;
    }

    private static String getGitRepoPath(final Path repoPath) throws IOException {
        // If it's a directory, we're probably good.
        // Otherwise, it's probably a submodule so we need to resolve the actual directory.
        if (Files.isDirectory(repoPath)) {
            return repoPath.toAbsolutePath().toString();
        } else {
            return repoPath.getParent().resolve(
                    new StringSearch(repoPath)
                            .searchMustMatch(Searcher.exactly("gitdir: "))
                            .nonmatching())
                    .toAbsolutePath().toString();
        }
    }

    public JavaProject build() throws GitAPIException, IOException {
        final Git git = new Git(new FileRepository(getGitRepoPath(repoPath)));

        final Set<CompilationUnit> files = parseJavaFiles(git, "");

        return new JavaProject(repoPath, path, ref, files, git);
    }

    private Set<CompilationUnit> parseJavaFiles(final Git git, final String filter)
            throws GitAPIException {
        setupSolver(git);

        return getAllFileContent(filter);
    }

    private void setupSolver(final Git git) throws GitAPIException {
        CombinedTypeSolver solver = createSolver(git);

        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(new JavaSymbolSolver(solver));
        JavaParser.setStaticConfiguration(parserConfiguration);
    }

    private Set<CompilationUnit> getAllFileContent(String filter) {
        final Set<CompilationUnit> result = new HashSet<>();
        File[] files =
                FileUtils.convertFileCollectionToFileArray(
                        FileUtils.listFiles(path.toFile(), new String[] {"java"}, true));
        for (final File file : files) {
            if (file.isFile() && (filter == null || file.getName().contains(filter) || filter.isEmpty())) {
                try (final FileInputStream input = new FileInputStream(file)) {
                    AstUtil.tryParse(input).ifPresent(result::add);
                } catch (IOException ignored) {}
            }
        }

        return result;
    }

    private CombinedTypeSolver createSolver(final Git git) throws GitAPIException {
        gitSetProjectCommit(git, true);

        // Add basic reflection solver.
        final CombinedTypeSolver solver = new CombinedTypeSolver();
        solver.add(new ReflectionTypeSolver());

        // Add source solvers.
        for (final File sourceDir : getSourceDirectories(path.toFile())) {
            solver.add(new JavaParserTypeSolver(sourceDir));
        }

        // Add all jars.
        final Set<String> addedJars = new HashSet<>();
        File[] files =
                FileUtils.convertFileCollectionToFileArray(
                        FileUtils.listFiles(path.toFile(), new String[] {"jar"}, true));
        for (final File file : files) {
            try {
                new JarFile(file.getCanonicalPath());

                // Don't add jars more than once if they appear multiple times in modules.
                if (!addedJars.contains(file.getName())) {
                    solver.add(new JarTypeSolver(file.getCanonicalPath()));
                    addedJars.add(file.getName());
                }
            } catch (IOException ignored) {}
        }

        return solver;
    }

    private List<File> getSourceDirectories(final File dir) {
        try {
            if (dir.isDirectory()) {
                // Don't look at these.
                if (FileUtil.inParent(dir.getCanonicalPath(), "target")) {
                    return new ArrayList<>();
                }

                if (dir.getCanonicalPath().endsWith("src/main/java") ||
                        dir.getCanonicalPath().endsWith("src/test/java") ||
                        dir.getCanonicalPath().endsWith("src/experimental")) {
                    return Collections.singletonList(dir);
                }

                final File[] files = dir.listFiles();
                if (files != null) {
                    return Arrays.stream(files)
                            .flatMap(d -> getSourceDirectories(d).stream())
                            .collect(Collectors.toList());
                }

                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private void gitSetProjectCommit(final Git git, boolean recompile)
            throws GitAPIException {
        // Reset the repository to the desired version.
        git.reset().setMode(ResetCommand.ResetType.HARD).setRef(ref).call();

        // Call maven, if we want to compile
        if (recompile) {
            MavenCli maven = new MavenCli();
            maven.doMain(new String[]{"clean", "install", "dependency:copy-dependencies"}, path.toAbsolutePath().toString(), System.out, System.out);
        }
    }
}
