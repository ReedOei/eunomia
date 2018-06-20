package com.reedoei.eunomia.string.matching;

import org.checkerframework.checker.nullness.qual.NonNull;

public class IndexMatch extends Match {
    private final int start;
    private final int end;

    public IndexMatch(@NonNull final String base, final int start, final int end) {
        super(base);

        this.start = start;
        this.end = end;
    }

    @NonNull
    @Override
    public String matching() {
        return base.substring(start, end);
    }

    @NonNull
    @Override
    public String nonmatching() {
        return base.substring(0, start) + base.substring(end);
    }
}
