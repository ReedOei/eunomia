package com.reedoei.eunomia.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangedFile {
    private final CompilationUnit oldFile;
    private final CompilationUnit newFile;
    private final String filename;
    private final JavaProject project;

    public static Optional<ChangedFile> create(final @Nullable CompilationUnit oldContent,
                                               final @Nullable CompilationUnit newContent,
                                               final String path,
                                               final JavaProject project) {
        if (oldContent != null && newContent != null) {
            return Optional.of(new ChangedFile(oldContent, newContent, path, project));
        } else {
            return Optional.empty();
        }
    }

    public ChangedFile(final CompilationUnit oldContent,
                       final CompilationUnit newContent,
                       final String path,
                       final JavaProject project) {
        this.filename = path;
        this.oldFile = oldContent;
        this.newFile = newContent;
        this.project = project;
    }

    private Set<MethodDeclaration> getTestMethods(CompilationUnit newFile) {
        return newFile.findAll(MethodDeclaration.class).stream()
                .filter(m -> m.isAnnotationPresent("Test") ||
                             m.getNameAsString().startsWith("test"))
                .collect(Collectors.toSet());
    }

    private Stream<MethodDeclaration> getAddedTests() {
        final Set<String> oldTestMethods = getTestMethods(oldFile).stream()
                .map(MethodDeclaration::getNameAsString)
                .collect(Collectors.toSet());

        return getTestMethods(newFile).stream()
                .filter(testMethod -> !oldTestMethods.contains(testMethod.getNameAsString()));
    }

    private Stream<MethodDeclaration> getModifiedTests() {
        final Set<MethodDeclaration> oldTestMethods = getTestMethods(oldFile);
        final Set<String> oldTestMethodNames = getTestMethods(oldFile).stream()
                .map(MethodDeclaration::getNameAsString)
                .collect(Collectors.toSet());

        return getTestMethods(newFile).stream()
                .filter(testMethod -> oldTestMethodNames.contains(testMethod.getNameAsString()) &&
                        !oldTestMethods.contains(testMethod));
    }

    private Stream<MethodDeclaration> getDeletedTests() {
        final Set<String> newTestMethods = getTestMethods(newFile).stream()
                .map(MethodDeclaration::getNameAsString)
                .collect(Collectors.toSet());

        return getTestMethods(oldFile).stream()
                .filter(testMethod -> !newTestMethods.contains(testMethod.getNameAsString()));
    }

    private Stream<MethodDeclaration> getTests(String type) {
        switch (type) {
            case "added":
                return getAddedTests();

            case "modified":
                return getModifiedTests();

            case "deleted":
                return getDeletedTests();

            default:
                return Stream.empty();
        }
    }

    public int getNumTests(final String type, final boolean print) {
        final Set<String> testMethods = getTests(type)
                .map(MethodDeclaration::getNameAsString)
                .collect(Collectors.toSet());

        if (print) {
            System.out.println("        " + type + " tests: " + testMethods);
        }

        return testMethods.size();
    }

    public String getFilename() {
        return filename;
    }
}
