package com.reedoei.eunomia.io;

import com.reedoei.eunomia.util.StandardMain;

import java.io.PrintStream;

public interface VerbosePrinter {
    /**
     * Returns the current verbosity. Generally, this is the only method you should override.
     */
    default int verbosity() {
        return 1;
    }

    default PrintStream printStream() {
        return System.out;
    }

    default int verbosity(final StandardMain main) {
        // use array so we can set it from inside the lambda below
        final int[] verbosity = {0};

        main.getArg("getVerbosity").ifPresent(v -> verbosity[0] = Integer.parseInt(v));

        if (main.getArg("verbose").isPresent()) {
            verbosity[0] = 1;
        }

        for (final String s : main.cleanArgs()) {
            // If we get an arg like -vvvv set the verbosity level to 4.
            if (s.replace("v", "").isEmpty()) {
                verbosity[0] = s.length();
            }
        }

        return verbosity[0];
    }

    default void print(final String s) {
        print(s, 1);
    }

    default void print(final String s, final int level) {
        if (verbosity() >= level) {
            printStream().print(s);
        }
    }

    default void printf(final String s, final Object... objects) {
        printf(s, 1, objects);
    }

    default void printf(final String s, final int level, final Object... objects) {
        print(String.format(s, objects), level);
    }

    default void println(final String s, final int level) {
        print(s + System.lineSeparator(), level);
    }

    default void println(final String s) {
        print(s + System.lineSeparator());
    }

    default void println(final int level) {
        println("", level);
    }

    default void println() {
        println("");
    }
}
