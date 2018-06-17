package com.reedoei.eunomia.string.matching;

import org.jetbrains.annotations.NotNull;

public class IndexMatch extends Match {
    private final int start;
    private final int end;

    public IndexMatch(@NotNull final String base, final int start, final int end) {
        super(base);

        this.start = start;
        this.end = end;
    }

    @NotNull
    @Override
    public String matching() {
        return base.substring(start, end);
    }

    @NotNull
    @Override
    public String nonmatching() {
        return base.substring(0, start) + base.substring(end);
    }
}
