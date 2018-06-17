package com.reedoei.eunomia.string.searching;

import com.reedoei.eunomia.string.matching.IndexMatch;
import com.reedoei.eunomia.string.matching.Match;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiPredicate;

public class IndexSearcher extends Searcher {
    private final BiPredicate<String, String> pred;
    private final String searchString;

    public IndexSearcher(final String searchString, final BiPredicate<String, String> pred) {
        this.searchString = searchString;
        this.pred = pred;
    }

    @NotNull
    @Override
    public Optional<Match> test(final String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.length() < (i + searchString.length())) {
                break;
            }

            if (pred.test(searchString, s.substring(i, i + searchString.length()))) {
                return Optional.of(new IndexMatch(s, i, i + searchString.length()));
            }
        }

        return Optional.empty();
    }
}
