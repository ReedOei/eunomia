package com.reedoei.eunomia.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

public class AstUtil {
    public static String qualifiedToPath(final String qualifiedName) {
        final String[] temp = qualifiedName.split("\\."); // regex
        return String.join("/", Arrays.asList(temp).subList(0, temp.length - 1));
    }

    public static Optional<CompilationUnit> tryParse(final InputStream code) {
        try {
            return Optional.ofNullable(JavaParser.parse(code));
        } catch (ParseProblemException ignored) {
            return Optional.empty();
        }
    }
}
