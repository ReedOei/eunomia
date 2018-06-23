package com.reedoei.eunomia.ast.resolved;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class ResolvedMethodCall {
    public final MethodCallExpr base;

    public final ResolvedMethodDeclaration invoked;

    public ResolvedMethodCall(final MethodCallExpr base) {
        this.base = base;

        invoked = base.resolve();
    }
}
