package com.reedoei.eunomia.string.searching;

import com.reedoei.eunomia.string.matching.Match;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.regex.Pattern;

public abstract class Searcher {
    public static Searcher exactly(final String s) {
        return new IndexSearcher(s, String::equals);
    }

    public static Searcher contains(final String s) {
        return new SubstringSearcher(s);
    }

    public static Searcher ignoreCase(final String s) {
        return new IndexSearcher(s, String::equalsIgnoreCase);
    }

    public static Searcher pattern(final String pattern) {
        return Searcher.pattern(Pattern.compile(pattern));
    }

    public static Searcher pattern(final Pattern pattern) {
        return new RegexSearcher(pattern);
    }

    @NonNull
    public abstract Optional<Match> test(String s);
}
