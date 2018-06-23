package com.reedoei.eunomia.string.searching;

import com.reedoei.eunomia.string.matching.IndexMatch;
import com.reedoei.eunomia.string.matching.Match;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSearcher extends Searcher {
    private final Pattern pattern;

    public RegexSearcher(final Pattern pattern) {
        this.pattern = pattern;
    }

    @NonNull
    @Override
    public Optional<Match> test(final String s) {
        final Matcher matcher = pattern.matcher(s);

        if (matcher.matches()) {
            return Optional.of(new IndexMatch(s, matcher.regionStart(), matcher.regionEnd()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public @NonNull String description() {
        return pattern.toString();
    }
}
