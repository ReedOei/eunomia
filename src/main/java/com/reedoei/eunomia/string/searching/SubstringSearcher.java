package com.reedoei.eunomia.string.searching;

import com.reedoei.eunomia.string.matching.Match;
import com.reedoei.eunomia.string.matching.SubstringMatch;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SubstringSearcher extends Searcher {
    private final String searchString;

    public SubstringSearcher(final String searchString) {
        this.searchString = searchString;
    }

    @NotNull
    @Override
    public Optional<Match> test(final String s) {
        if (s.contains(searchString)) {
            return Optional.of(new SubstringMatch(s, searchString));
        } else {
            return Optional.empty();
        }
    }
}
