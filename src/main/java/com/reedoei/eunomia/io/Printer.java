package com.reedoei.eunomia.io;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Printer {
    // TODO: Add coordinated printer so that we can do things like:
    // print("Test "); print(" ing"); and only get one spcae
    // or:
    // print("Done."); print(", result is") and get "Done, result is".
    // or:
    // print("123"); print("456"); is "123 456"
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
    private String constantLineText = "";

    public Printer(final PrintStream stream) {
        this.stream = stream;
    }

    // Makes the line text safe. TODO: Should this be text suitable for printing via clearline always?
    // Probably. Then we can always do the same print operation.
    private String makeSafe(final String s) {
        return s;
    }

    public void print(final String s) {
        stream.print(s);
    }

    public void printTemporary(final String s) {
        stream.print(s);
        currentLine += s;
    }

    public void clearLine() {
        clearLine("");
    }

    public void clearLine(final String s) {
        IOUtil.printClearLine(stream, s);
        currentLine = s;
        constantLineText = s;
    }

    public void println() {
        println("");
    }

    public void println(final String s) {
        stream.println(s);
    }
}
