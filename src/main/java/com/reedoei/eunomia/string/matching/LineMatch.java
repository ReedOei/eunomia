package com.reedoei.eunomia.string.matching;

import com.reedoei.eunomia.string.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LineMatch extends Match {
    private final List<String> lines;
    private final int lineNum;

    private final Match match;

    /* TODO: Added matching and nonmatching part.
     This is often useful for files that have things like:
     "Value: x"
    So if we search for "Value:" can we eliminate the nonmatching part and just get "x"
    */

    public LineMatch(final Match match, final List<String> lines, final int lineNum) {
        super(lines.get(lineNum));

        this.match = match;
        this.lines = lines;
        this.lineNum = lineNum;
    }

    public String get() {
        return base;
    }

    public Context before(final int lineCount) {
        return Context.from(lines, lineNum - lineCount, lineNum );
    }

    public Context beforeInc(final int lineCount) {
        return Context.from(lines, lineNum - lineCount, lineNum + 1);
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

    @Override
    public String toString() {
        return get();
    }

    @NotNull
    @Override
    public String matching() {
        return base;
    }

    @NotNull
    @Override
    public String nonmatching() {
        return match.nonmatching();
    }
}
