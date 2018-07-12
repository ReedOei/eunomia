package com.reedoei.eunomia.ast.instrumentation;

import com.reedoei.eunomia.util.StandardMain;

// TODO: Make it easy to call a custom instrumenter.
// TODO: Add instrument() static method that starts new process (because that's what Soot needs), see
//       dt fixing tools.
public class Instrumentation extends StandardMain {
    public Instrumentation(String[] args) {
        super(args);
    }

    @Override
    protected void run() throws Exception {

    }
}
