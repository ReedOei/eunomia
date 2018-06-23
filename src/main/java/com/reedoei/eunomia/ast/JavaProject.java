package com.reedoei.eunomia.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.MethodUsage;
import com.reedoei.eunomia.ast.resolved.ResolvedClass;
import com.reedoei.eunomia.util.NOptionalBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaProject {
    private final Path repoPath;
    private final Path path;
    private final String ref;
    private final Git git;

    private final Set<ClassOrInterfaceDeclaration> classes = new HashSet<>();
    private final Set<ResolvedClass> resolvedClasses = new HashSet<>();
    private final Set<CompilationUnit> files;

    public JavaProject(final Path repoPath,
                       final Path path,
                       final String ref,
                       final Set<CompilationUnit> files,
                       final Git git) {
        this.repoPath = repoPath;
        this.path = path;
        this.ref = ref;

        this.git = git;

        this.files = files;

        for (final CompilationUnit file : files) {
            classes.addAll(file.findAll(ClassOrInterfaceDeclaration.class));
        }

        for (final ClassOrInterfaceDeclaration c : classes) {
            resolvedClasses.add(new ResolvedClass(c));
        }
    }

    public List<ChangedFile> getChangedFileContent(final String newCommitId) {
        final Set<String> changedFileNames = changedFiles(newCommitId);

        final List<ChangedFile> fileContent = new ArrayList<>();

        for (final String path : changedFileNames) {
            if (FilenameUtils.isExtension(path, "java")) {
                new NOptionalBuilder<String, CompilationUnit>()
                        .add("old", getFileContent(ref, path))
                        .add("new", getFileContent(newCommitId, path))
                        .build().ifPresent(m ->
                            ChangedFile.create(m.get("old"), m.get("new"), path, this)
                                    .ifPresent(fileContent::add)
                        );
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

    public String commitBefore(final Date date) throws GitAPIException {
        return git.log()
                .setRevFilter(CommitTimeRevFilter.before(date))
                .setMaxCount(1)
                .call().iterator().next().getName();
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

        final File[] files =
                FileUtils.convertFileCollectionToFileArray(
                        FileUtils.listFiles(path.toFile(), new String[] {"java"}, true));
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
                                AstUtil.tryParse(input)
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
                                AstUtil.tryParse(input)
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
                    AstUtil.tryParse(input)
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

    private Optional<CompilationUnit> getFileContent(final String ref, final String filePath) {
        try {
            // From: https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/api/ReadFileFromCommit.java
            final ObjectId lastCommitId = git.getRepository().resolve(ref);
            if (lastCommitId == null) {
                return Optional.empty();
            }

            // a RevWalk allows to walk over commits based on some filtering that is defined
            try (final RevWalk revWalk = new RevWalk(git.getRepository())) {
                final RevCommit commit = revWalk.parseCommit(lastCommitId);
                // and using commit's tree find the path
                final RevTree tree = commit.getTree();

                // now try to find a specific file
                try (final TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    treeWalk.setFilter(PathFilter.create(filePath));

                    if (treeWalk.next()) {
                        if (FilenameUtils.isExtension(treeWalk.getPathString(), "java")) {
                            final ObjectId objectId = treeWalk.getObjectId(0);

                            revWalk.dispose();
                            return AstUtil.tryParse(git.getRepository().open(objectId).openStream());
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

    private Set<String> changedFiles(final String commitId) {
        final Set<String> pathList = new HashSet<>();

        try {
            final ObjectId commit = git.getRepository().resolve(commitId + "^{tree}");
            final ObjectId prevCommit = git.getRepository().resolve(commitId + "~1^{tree}");

            if (commit == null || prevCommit == null) {
                return new HashSet<>();
            }

            try (final ObjectReader reader = git.getRepository().newObjectReader()) {
                final CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, prevCommit);

                final CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, commit);

                // finally get the list of changed files
                try {
                    final List<DiffEntry> diffs = git.diff()
                            .setNewTree(newTreeIter)
                            .setOldTree(oldTreeIter)
                            .call();

                    for (final DiffEntry entry : diffs) {
                        pathList.add(entry.getNewPath());
                    }
                } catch (GitAPIException e) {
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

    public Git getGit() {
        return git;
    }

    public void close() {
        git.getRepository().close();
        git.close();
    }
}
