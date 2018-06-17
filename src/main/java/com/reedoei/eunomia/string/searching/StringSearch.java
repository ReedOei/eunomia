package com.reedoei.eunomia.string.searching;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import com.reedoei.eunomia.string.matching.LineMatch;
import com.reedoei.eunomia.string.matching.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class StringSearch {
    @NotNull
    private final String base;

    @NotNull
    private final List<String> lines;

    public StringSearch(@NotNull final String base) {
        this.base = base;

        final String[] split = base.split(System.lineSeparator());
        Preconditions.checkNotNull(split);
        this.lines = Arrays.asList(split);
    }

    @NotNull
    public Stream<LineMatch> search(final Searcher searcher) {
        // Create an iterator so that we can create a stream so that it will be lazily evaluated.
        final Iterator<LineMatch> iterator = new Iterator<LineMatch>() {
            private int i = 0;

            @Nullable
            private LineMatch next = null;

            private LineMatch findNext() {
                for (; i < lines.size(); i++) {
                    final String line = lines.get(i);

                    final Optional<Match> match = searcher.test(line);

                    if (match.isPresent()) {
                        i++; // Increment because we won't go through the loop when we return.
                        return new LineMatch(match.get(), lines, i - 1);
                    }
                }

                return null;
            }

            @Override
            public boolean hasNext() {
                if (next == null) {
                    next = findNext();
                }

                return next != null;
            }

            @Override
            public LineMatch next() {
                final LineMatch temp = next;
                next = null;
                return temp;
            }
        };

        return Streams.stream(iterator);
    }
}
