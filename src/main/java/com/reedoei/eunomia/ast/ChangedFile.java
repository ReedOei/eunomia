package com.reedoei.eunomia.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChangedFile {
    private final CompilationUnit oldFile;
    private final CompilationUnit newFile;
    private final String filename;
    private final JavaProject project;

    public ChangedFile(final CompilationUnit oldContent,
                       final CompilationUnit newContent,
                       final String path,
                       final JavaProject project) {
        this.filename = path;
        this.oldFile = oldContent;
        this.newFile = newContent;
        this.project = project;
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

    private Predicate<MethodCallExpr> methodCallsSame(final MethodCallExpr a) {
        try {
            final ResolvedMethodDeclaration aMethod = a.resolveInvokedMethod();

            return b -> {
                try {
                    final ResolvedMethodDeclaration bMethod = b.resolveInvokedMethod();

                    return aMethod.getQualifiedSignature().equals(bMethod.getQualifiedSignature());
                } catch (Exception e) {
//                    System.out.println("Failed to resolve: " + b.getNameAsString());
//                    e.printStackTrace();
                    return false;
                }
            };
        } catch (Exception e) {
//            System.out.println("Failed to resolve: " + a.getNameAsString());
//            e.printStackTrace();
            return b -> false;
        }
    }

    private Stream<MethodDeclaration> getTestMethodsThatCall(MethodCallExpr mCall) {
        return getTestMethods()
                .filter(testMethod -> testMethod.findAll(MethodCallExpr.class).stream()
                        .anyMatch(methodCallsSame(mCall)));
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
