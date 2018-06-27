package com.reedoei.eunomia.io;

public class IOUtil {
    public static void printClearLine(final String s) {
        System.out.print(String.format("\r\033[2K%s", s));
    }
}
