package com.reedoei.eunomia.string.searching;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import com.reedoei.eunomia.string.matching.LineMatch;
import com.reedoei.eunomia.string.matching.Match;
import com.reedoei.eunomia.util.FileUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class StringSearch {
    @NonNull
    private final String base;

    @NonNull
    private final List<String> lines;

    public StringSearch(@NonNull final String base) {
        this.base = base;

        final String[] split = base.split(System.lineSeparator());
        Preconditions.checkNotNull(split);
        this.lines = new ArrayList<>(Arrays.asList(split));
    }

    public StringSearch(final Path repoPath) throws IOException {
        this(FileUtil.readFile(repoPath));
    }

    public LineMatch searchMustMatch(final Searcher searcher) {
        return searchFirst(searcher).orElseThrow(() -> new IllegalArgumentException("No match found for: " + searcher.description()));
    }

    @NonNull
    public Optional<LineMatch> searchFirst(final Searcher searcher) {
        return search(searcher).findFirst();
    }

    @NonNull
    public Stream<LineMatch> search(final Searcher searcher) {
        // Create an iterator so that we can create a stream so that it will be lazily evaluated.
        final Iterator<LineMatch> iterator = new Iterator<LineMatch>() {
            private int i = 0;

            @Nullable
            private LineMatch next = null;

            @Nullable
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

                if (temp != null) {
                    return temp;
                } else {
                    throw new IllegalStateException("next() was called when there are no more elements!");
                }
            }
        };

        return Streams.stream(iterator);
    }
}
