package com.reedoei.eunomia.ast.resolved;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;

import java.util.HashSet;
import java.util.Set;

public class ResolvedClass {
    public final Set<ResolvedMethod> methods = new HashSet<>();
    public final Set<ResolvedFieldDeclaration> fields = new HashSet<>();
    public final Set<ResolvedType> annotations = new HashSet<>();
    public final Set<ResolvedReferenceType> superTypes = new HashSet<>();
    public final ClassOrInterfaceDeclaration base;
    public final ResolvedReferenceTypeDeclaration declaration;

    public ResolvedClass(final ClassOrInterfaceDeclaration base) {
        this.base = base;

        declaration = base.resolve();

        for (final MethodDeclaration m : base.findAll(MethodDeclaration.class)) {
            methods.add(new ResolvedMethod(m));
        }

        for (final FieldDeclaration f : base.getFields()) {
            fields.add(f.resolve());
        }

        for (final ClassOrInterfaceType c : base.getImplementedTypes()) {
            superTypes.add(c.resolve());
        }

        for (final ClassOrInterfaceType c : base.getExtendedTypes()) {
            superTypes.add(c.resolve());
        }

        for (final AnnotationExpr a : base.getAnnotations()) {
            annotations.add(a.calculateResolvedType());
        }
    }
}
