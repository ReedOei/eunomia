package com.reedoei.eunomia.ast.resolved;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.reedoei.eunomia.util.OptUtil;

import java.util.ArrayList;
import java.util.List;

public class ResolvedMethod {
    public final List<ResolvedMethodCall> methodCalls = new ArrayList<>();

    public final MethodDeclaration base;
    public final ResolvedMethodDeclaration declaration;

    public ResolvedMethod(final MethodDeclaration base) {
        this.base = base;

        declaration = base.resolve();

        for (final MethodCallExpr calls : base.findAll(MethodCallExpr.class)) {
            methodCalls.add(new ResolvedMethodCall(calls));
        }

        // Sort by line, then by column.
        methodCalls.sort((a, b) ->
            OptUtil.map(a.base.getRange(), b.base.getRange(), (aRange, bRange) -> {
                if (aRange.begin.line < bRange.begin.line ||
                        (aRange.begin.line == bRange.begin.line && aRange.begin.column < bRange.begin.column)) {
                    return -1;
                } else if (aRange.begin.line == bRange.begin.line &&
                        aRange.begin.column == bRange.begin.column){
                    return 0;
                } else {
                    return 1;
                }
            }).orElse(0));
    }
}
