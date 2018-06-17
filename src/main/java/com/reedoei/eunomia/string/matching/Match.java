package com.reedoei.eunomia.string.matching;

import org.jetbrains.annotations.NotNull;

public abstract class Match {
    @NotNull
    protected final String base;

    public Match(@NotNull final String base) {
        this.base = base;
    }

    @NotNull
    public abstract String matching();

    @NotNull
    public abstract String nonmatching();
}
