package com.reedoei.eunomia.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.reedoei.eunomia.ast.resolved.ResolvedClass;
import com.reedoei.eunomia.util.FileUtil;
import com.reedoei.eunomia.util.NOptionalBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.cli.MavenCli;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaProject {
    private final String repoPath;
    private final String path;
    private final String ref;
    private final Repository repository;
    private final Set<ClassOrInterfaceDeclaration> classes = new HashSet<>();
    private final Set<ResolvedClass> resolvedClasses = new HashSet<>();
    private final Set<CompilationUnit> files;

    public JavaProject(final String repoPath, final String ref) throws IOException {
        this.repoPath = repoPath;
        this.path = Paths.get(repoPath).getParent().toString();
        this.ref = ref;

        this.repository = new FileRepository(repoPath);

        files = parseJavaFiles(ref, "");

        for (final CompilationUnit file : files) {
            classes.addAll(file.findAll(ClassOrInterfaceDeclaration.class));
        }

        for (final ClassOrInterfaceDeclaration c : classes) {
            resolvedClasses.add(new ResolvedClass(c));
        }
    }

    private void setupSolver(final String commitId) {
        CombinedTypeSolver solver = createSolver(commitId);

        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(new JavaSymbolSolver(solver));
        JavaParser.setStaticConfiguration(parserConfiguration);
    }

    private Set<CompilationUnit> parseJavaFiles(final String commitId, final String filter) {
        setupSolver(commitId);

        return getAllFileContent(filter);
    }

    public List<ChangedFile> getChangedFileContent(final String newCommitId) {
        final Set<String> changedFileNames = getChangedFilesInCommit(newCommitId);

        final List<ChangedFile> fileContent = new ArrayList<>();

        for (final String path : changedFileNames) {
            if (FilenameUtils.isExtension(path, "java")) {
                new NOptionalBuilder<String, CompilationUnit>()
                        .add("old", getFileContent(ref, path))
                        .add("new", getFileContent(newCommitId, path))
                        .build().ifPresent(m ->
                        fileContent.add(new ChangedFile(m.get("old"), m.get("new"), path, this)));
            }
        }

        return fileContent;
    }

    public Stream<MethodDeclaration> testMethods() {
        return classes.stream()
                .flatMap(cu -> cu.findAll(MethodDeclaration.class).stream()
                        .filter(m -> m.isAnnotationPresent("Test") ||
                                m.getNameAsString().startsWith("test")));
    }

    private static String unqualifyName(final String qualifiedName) {
        final String[] temp = qualifiedName.split("\\.");
        return temp[temp.length - 1];
    }

    private static Optional<CompilationUnit> tryParse(final InputStream code) {
        try {
            return Optional.ofNullable(JavaParser.parse(code));
        } catch (ParseProblemException ignored) {
            return Optional.empty();
        }
    }

    public String getCommitIdFromTimestamp(final String timestamp) throws GitAPIException {
        return new Git(repository).log()
                .setRevFilter(CommitTimeRevFilter.before(new Date(timestamp)))
                .setMaxCount(1)
                .call().iterator().next().getName();
    }

    private void gitSetProjectCommit(final String ref, boolean recompile)
            throws GitAPIException {
        // Reset the repository to the desired version.
        new Git(repository).reset().setMode(ResetCommand.ResetType.HARD).setRef(ref).call();

        // Call maven, if necessary to compile

        if (recompile) {
            MavenCli maven = new MavenCli();
            maven.doMain(new String[]{"clean", "install", "dependency:copy-dependencies"}, path, System.out, System.out);
        }
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

    private CombinedTypeSolver createSolver(final String ref) {
        try {
            gitSetProjectCommit(ref, true);
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        // Add basic reflection solver.
        final CombinedTypeSolver solver = new CombinedTypeSolver();
        solver.add(new ReflectionTypeSolver());

        // Add source solvers.
        for (final File sourceDir : getSourceDirectories(new File(path))) {
            solver.add(new JavaParserTypeSolver(sourceDir));
        }

        // Add all jars.
        final Set<String> addedJars = new HashSet<>();
        File[] files =
                FileUtils.convertFileCollectionToFileArray(
                        FileUtils.listFiles(new File(path), new String[] {"jar"}, true));
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

    private Set<String> getInheritedCandidates(final CompilationUnit cu,
                                               final Set<String> methodNames) {
        final Set<String> result = new HashSet<>();

        final Set<String> unqualifiedNames = methodNames.stream()
                .map(JavaProject::unqualifyName)
                .collect(Collectors.toSet());

        for (final ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            try {
                for (MethodUsage mu : cls.resolve().getAllMethods()) {
                    if (unqualifiedNames.contains(mu.getName())) {
                        result.add(mu.getDeclaration().getQualifiedName());
                    }
                }
            } catch (Exception | StackOverflowError ignored) {}
        }

        return result;
    }

    public List<MethodDeclaration> findMethodDeclarations(final Set<String> testMethodNames) {
        final Set<String> toFind = new HashSet<>(testMethodNames);
        final Set<String> newToFind = new HashSet<>(testMethodNames);
        final List<MethodDeclaration> result = new ArrayList<>();

        File[] files =
                FileUtils.convertFileCollectionToFileArray(
                        FileUtils.listFiles(new File(path), new String[] {"java"}, true));
        // Add all inherited methods that match one of the test names we're looking for.
//        TODO: get guessed paths
//                filter by that
        final Set<String> guessedSets = testMethodNames.stream().map(AstUtil::qualifiedToPath).collect(Collectors.toSet());
        for (final File file : files) {
            if (toFind.isEmpty()) {
                break;
            }
            if (file.isFile()) {
                for (final String name : toFind) {
                    try {
                        final String guessedPath = AstUtil.qualifiedToPath(name);
                        if (file.getCanonicalPath().contains(guessedPath)) {
                            try (final FileInputStream input = new FileInputStream(file)) {
                                tryParse(input)
                                        .map(cu -> getInheritedCandidates(cu, testMethodNames))
                                        .ifPresent(newToFind::addAll);

                                input.close();
                            } catch (IOException ignored) {}
                        }
                    } catch (IOException ignored) {}
                }
            }
        }

        // Loop through and see if we can find by path first, because it's cheaper
        for (final File file : files) {
            if (toFind.isEmpty()) {
                break;
            }
            if (file.isFile()) {
                for (final String name : toFind) {
                    try {
                        final String[] temp = name.split("\\."); // regex
                        final String guessedPath = String.join("/", Arrays.asList(temp).subList(0, temp.length - 1));
//                        final String methodName = temp[temp.length - 1];
                        if (file.getCanonicalPath().contains(guessedPath)) {
                            try (final FileInputStream input = new FileInputStream(file)) {
                                tryParse(input)
                                        .ifPresent(cu ->
                                                cu.findAll(MethodDeclaration.class)
                                                        .forEach(method -> {
                                                            if (newToFind.stream().anyMatch(m -> m.endsWith(method.getNameAsString()))) {
                                                                try {
                                                                    final String methodName = method.resolve().getQualifiedName();
                                                                    if (newToFind.contains(methodName)) {
                                                                        result.add(method);
                                                                        newToFind.remove(methodName);
                                                                    }
                                                                } catch (Exception ignored) {
                                                                    System.out.println("Failed to resolve: " + method.getNameAsString());
                                                                }
                                                            }
                                                        }));

                                input.close();
                            } catch (IOException ignored) {}
                        }
                    } catch (IOException ignored) {}
                }
            }
        }

        for (final File file : files) {
            if (newToFind.isEmpty()) {
                break;
            }
            if (file.isFile()) {
                try (final FileInputStream input = new FileInputStream(file)) {
                    tryParse(input)
                            .ifPresent(cu -> cu.findAll(MethodDeclaration.class)
                                    .forEach(method -> {
                                        if (newToFind.stream().anyMatch(m -> m.endsWith(method.getNameAsString()))) {
                                            try {
                                                final String methodName = method.resolve().getQualifiedName();
                                                if (newToFind.contains(methodName)) {
                                                    result.add(method);
                                                }
                                            } catch (Exception ignored) {
                                                System.out.println("Failed to resolve: " + method.getNameAsString());
                                            }
                                        }
                                    }));

                    input.close();
                } catch (IOException ignored) {}
            }
        }

        return result;
    }

    private Set<CompilationUnit> getAllFileContent(String filter) {
        final Set<CompilationUnit> result = new HashSet<>();
        File[] files =
                FileUtils.convertFileCollectionToFileArray(
                        FileUtils.listFiles(new File(path), new String[] {"java"}, true));
        for (final File file : files) {
            if (file.isFile() && (filter == null || file.getName().contains(filter) || filter.isEmpty())) {
                try (final FileInputStream input = new FileInputStream(file)) {
                    tryParse(input)
                            .ifPresent(result::add);
                } catch (IOException ignored) {}
            }
        }

        return result;
    }

    private Optional<CompilationUnit> getFileContent(final String ref, final String filePath) {
        try {
            // From: https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/api/ReadFileFromCommit.java
            ObjectId lastCommitId = repository.resolve(ref);

            // a RevWalk allows to walk over commits based on some filtering that is defined
            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(lastCommitId);
                // and using commit's tree find the path
                RevTree tree = commit.getTree();

                // now try to find a specific file
                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    treeWalk.setFilter(PathFilter.create(filePath));

                    if (treeWalk.next()) {
                        if (FilenameUtils.isExtension(treeWalk.getPathString(), "java")) {
                            ObjectId objectId = treeWalk.getObjectId(0);

                            revWalk.dispose();
                            return tryParse(repository.open(objectId).openStream());
                        }
                    }

                    revWalk.dispose();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private Set<String> getChangedFilesInCommit(final String commitId) {
        final Set<String> pathList = new HashSet<>();

        try {
            final ObjectId commit = repository.resolve(commitId + "^{tree}");
            final ObjectId prevCommit = repository.resolve(commitId + "~1^{tree}");

            try (final ObjectReader reader = repository.newObjectReader()) {
                final CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, prevCommit);

                final CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, commit);

                // finally get the list of changed files
                try (final Git git = new Git(repository)) {
                    List<DiffEntry> diffs= git.diff()
                            .setNewTree(newTreeIter)
                            .setOldTree(oldTreeIter)
                            .call();

                    for (final DiffEntry entry : diffs) {
                        pathList.add(entry.getNewPath());
                    }
                } catch (final GitAPIException e) {
                    e.printStackTrace();
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return pathList;
    }

    public Set<ResolvedClass> classes() {
        return resolvedClasses;
    }

    public Repository getRepository() {
        return repository;
    }
}
