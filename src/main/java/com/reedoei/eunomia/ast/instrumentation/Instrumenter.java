package com.reedoei.eunomia.ast.instrumentation;

import soot.Body;
import soot.BodyTransformer;
import soot.PatchingChain;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Add methods to do things like visit specific statement types/add statements
// TODO: Add methods to make it easier to generate things like try/catch blocks
public abstract class Instrumenter extends BodyTransformer {
    private final List<String> args;
    private String methodName = "";
    private Body body = new JimpleBody();
    private PatchingChain<Unit> units = body.getUnits();

    private String phaseName = "";
    private Map<String, String> options = new HashMap<>();

    public Instrumenter() {
        this.args = new ArrayList<>();
    }

    public Instrumenter(final List<String> args) {
        this.args = args;
    }

    @Override
    protected void internalTransform(final Body b, final String phaseName, final Map<String, String> options) {
        this.methodName = b.getMethod().getName();
        this.body = b;
        this.units = b.getUnits();

        this.phaseName = phaseName;
        this.options = options;

        for (final Unit unit : units) {
            visit(unit);
        }
    }

    protected abstract void visit(final InvokeStmt invokeStmt);
    protected abstract void visit(final ThrowStmt throwStmt);
    protected abstract void visit(final Stmt stmt);
    protected abstract void visit(final Unit unit);
}
