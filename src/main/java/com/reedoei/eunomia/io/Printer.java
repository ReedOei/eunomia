package com.reedoei.eunomia.io;

import com.reedoei.eunomia.string.CharUtil;
import com.reedoei.eunomia.string.StringUtil;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Printer {
    // TODO: Add coordinated printer so that we can do things like:
    // print("Done."); print(", result is") and get "Done, result is".
    // or:
    // print("Line start:"); printIntermediate(" progress here "); println(" line end");
    // First is Line start: progress here, then becomes "Line start: line end".

    private static Printer out = new Printer(System.out);

    public static Printer out() {
        return out;
    }

    private final PrintStream stream;

    private final List<String> lines = new ArrayList<>();
    private String currentLine = "";
    private String currentLineConst = "";

    public Printer(final PrintStream stream) {
        this.stream = stream;
    }

    // Makes the line text safe. TODO: Should this be text suitable for printing via clearline always?
    // Probably. Then we can always do the same print operation.
    private String makeSafe(String base, String s) {
        // Don't want to have double/triple/etc. spaces.
        if (base.endsWith(" ")) {
            while (s.startsWith(" ")) {
                s = s.substring(1);
            }
        } else {
            // But make sure we have at least one space if we're not starting a newline.
            if (!base.isEmpty() && !s.startsWith(" ")) {
                s = " " + s;
            }
        }

        if (StringUtil.lastChar(base).map(CharUtil::isPunctuation).orElse(false) &&
            StringUtil.firstChar(s).map(CharUtil::isPunctuation).orElse(false)) {
            base = StringUtil.removeLast(CharUtil::isPunctuation, base);
        }

        return base + s;
    }

    public void print(final String s) {
        currentLineConst = makeSafe(currentLineConst, s);
        currentLine = currentLineConst;

        IOUtil.printClearLine(stream, currentLineConst);
    }

    public void printTemporary(final String s) {
        currentLine = makeSafe(currentLine, s);

        IOUtil.printClearLine(stream, currentLine);
    }

    public void clearLine() {
        clearLine("");
    }

    public void clearLine(final String s) {
        currentLine = s;
        currentLineConst = s;

        IOUtil.printClearLine(stream, s);
    }

    public void println() {
        println("");
    }

    public void println(final String s) {
        // End the current line if we've got one going.
        if (!currentLine.isEmpty()) {
            stream.println();
            currentLine = "";
            currentLineConst = "";
        }

        stream.println(s);
    }
}
