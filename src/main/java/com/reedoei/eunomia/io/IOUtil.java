package com.reedoei.eunomia.io;

import java.io.PrintStream;

public class IOUtil {
    public static void printClearLine(final String s) {
        printClearLine(System.out, s);
    }

    public static void printClearLine(final PrintStream stream, final String s) {
        stream.print(String.format("\r\033[2K%s", s));
    }
}
