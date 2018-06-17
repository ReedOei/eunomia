package com.reedoei.eunomia.string.matching;

import org.jetbrains.annotations.NotNull;

public class WholeMatch extends Match {
    public WholeMatch(@NotNull String base) {
        super(base);
    }

    @NotNull
    @Override
    public String matching() {
        return base;
    }

    @NotNull
    @Override
    public String nonmatching() {
        return "";
    }
}
