package com.reedoei.eunomia.string.matching;

import org.checkerframework.checker.nullness.qual.NonNull;

public class SubstringMatch extends Match {
    private final String match;

    public SubstringMatch(@NonNull final String base, final String match) {
        super(base);

        this.match = match;
    }

    @NonNull
    @Override
    public String matching() {
        return match;
    }

    @NonNull
    @Override
    public String nonmatching() {
        return base.replace(match, "");
    }
}
