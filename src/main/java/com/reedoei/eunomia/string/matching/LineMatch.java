package com.reedoei.eunomia.string.matching;

import com.reedoei.eunomia.string.Context;
import com.reedoei.eunomia.util.Util;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LineMatch extends Match {
    private final List<String> lines;
    private int lineNum;

    private final Match match;

    public LineMatch(final Match match, final List<String> lines, final int lineNum) {
        super(lines.get(lineNum));

        this.match = match;
        this.lines = lines;
        this.lineNum = lineNum;
    }

    public String get() {
        return base;
    }

    public Context before() {
        return Context.from(lines, 0, lineNum);
    }

    public Context before(final int lineCount) {
        return Context.from(lines, lineNum - lineCount, lineNum );
    }

    public Context beforeInc(final int lineCount) {
        return Context.from(lines, lineNum - lineCount, lineNum + 1);
    }

    public Context after() {
        return Context.from(lines, lineNum + 1, lines.size());
    }

    public Context after(final int lineCount) {
        return Context.from(lines, lineNum + 1, lineNum + lineCount + 1);
    }

    public Context afterInc(final int lineCount) {
        return Context.from(lines, lineNum, lineNum + lineCount + 1);
    }

    public Context context(final int lineCount) {
        return before(lineCount).add(after(lineCount));
    }

    public Context contextInc(final int lineCount) {
        // Not both Inc so we don't include this line twice.
        return beforeInc(lineCount).add(after(lineCount));
    }

    public Optional<LineMatch> prevLine() {
        return prevLine(1);
    }

    public Optional<LineMatch> prevLine(final int offset) {
        return nextLine(-offset);
    }

    public Optional<LineMatch> nextLine() {
        return nextLine(1);
    }

    public Optional<LineMatch> nextLine(final int offset) {
        if (Util.inRange(lineNum + offset, 0, lines.size())) {
            return Optional.of(new LineMatch(new WholeMatch(lines.get(lineNum + offset)), lines, lineNum + offset));
        } else {
            return Optional.empty();
        }
    }

    public LineMatch insertBefore(final String s) {
        return insertBefore(Collections.singletonList(s));
    }

    public LineMatch insertBefore(final List<String> ss) {
        lines.addAll(lineNum, ss);
        lineNum += ss.size();

        return this;
    }

    public LineMatch insertAfter(final String s) {
        return insertAfter(Collections.singletonList(s));
    }

    public LineMatch insertAfter(final List<String> ss) {
        lines.addAll(lineNum + 1, ss);
        return this;
    }

    /**
     * WARNING: This function will discard all match info and copies ALL lines.
     */
    public LineMatch map(final Function<String, String> f) {
        final List<String> newLines = new ArrayList<>(lines);
        newLines.set(lineNum, f.apply(base));

        return new LineMatch(new WholeMatch(newLines.get(lineNum)), newLines, lineNum);
    }

    public List<String> getLines() {
        return lines;
    }

    public Context delete() {
        return before().add(after());
    }

    public Context getContext() {
        return Context.from(lines, 0, lines.size());
    }

    @Override
    public String toString() {
        return get();
    }

    @NonNull
    @Override
    public String matching() {
        return base;
    }

    @NonNull
    @Override
    public String nonmatching() {
        return match.nonmatching();
    }
}
