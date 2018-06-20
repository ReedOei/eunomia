package com.reedoei.eunomia.string.matching;

import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class Match {
    @NonNull
    protected final String base;

    public Match(@NonNull final String base) {
        this.base = base;
    }

    @NonNull
    public abstract String matching();

    @NonNull
    public abstract String nonmatching();
}
