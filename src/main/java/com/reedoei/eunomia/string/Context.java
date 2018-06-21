package com.reedoei.eunomia.string;

import com.reedoei.eunomia.string.matching.LineMatch;
import com.reedoei.eunomia.string.matching.WholeMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Context {
    private final List<LineMatch> lines;

    public static Context from(final List<String> lines,
                               final int start,
                               final int end) {
        final int actualStart = Math.max(0, start);
        final int actualEnd = Math.min(lines.size(), end);

        final List<LineMatch> relevant = new ArrayList<>();

        for (int i = actualStart; i < actualEnd; i++) {
            relevant.add(new LineMatch(new WholeMatch(lines.get(i)), lines, i));
        }

        return new Context(relevant);
    }

    public static Function<LineMatch, Context> before(final int lineCount) {
        return l -> l.before(lineCount);
    }

    public static Function<LineMatch, Context> beforeInc(final int lineCount) {
        return l -> l.beforeInc(lineCount);
    }

    public static Function<LineMatch, Context> after(final int lineCount) {
        return l -> l.after(lineCount);
    }

    public static Function<LineMatch, Context> afterInc(final int lineCount) {
        return l -> l.afterInc(lineCount);
    }

    public static Function<LineMatch, Context> context(final int lineCount) {
        return l -> l.context(lineCount);
    }

    public static Function<LineMatch, Context> contextInc(final int lineCount) {
        return l -> l.contextInc(lineCount);
    }

    public static <T> Function<Context, T> apply(final Function<String, T> f) {
        return c -> c.map(f);
    }

    public static <T> Function<LineMatch, T> applyNextLine(final Function<String, T> f) {
        return after(1).andThen(apply(f));
    }

    public Context(final List<LineMatch> lines) {
        this.lines = lines;
    }

    public Context add(final Context other) {
        lines.addAll(other.lines);
        return this;
    }

    public Stream<String> lines() {
        return lines.stream().map(LineMatch::get);
    }

    public List<LineMatch> getLines() {
        return lines;
    }

    public <T> T map(final Function<String, T> f) {
        return f.apply(toString());
    }

    @Override
    public String toString() {
        return lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
