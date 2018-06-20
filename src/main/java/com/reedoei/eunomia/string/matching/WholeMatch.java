package com.reedoei.eunomia.string.matching;

import org.checkerframework.checker.nullness.qual.NonNull;

public class WholeMatch extends Match {
    public WholeMatch(@NonNull String base) {
        super(base);
    }

    @NonNull
    @Override
    public String matching() {
        return base;
    }

    @NonNull
    @Override
    public String nonmatching() {
        return "";
    }
}
