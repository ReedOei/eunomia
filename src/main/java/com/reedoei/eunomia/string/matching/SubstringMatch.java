package com.reedoei.eunomia.string.matching;

import org.jetbrains.annotations.NotNull;

public class SubstringMatch extends Match {
    private final String match;

    public SubstringMatch(@NotNull final String base, final String match) {
        super(base);

        this.match = match;
    }

    @NotNull
    @Override
    public String matching() {
        return match;
    }

    @NotNull
    @Override
    public String nonmatching() {
        return base.replace(match, "");
    }
}
