package com.reedoei.eunomia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class StandardMain {
    private static String cleanArg(@NonNull final String arg) {
        String result = arg;
        while (result.startsWith("-")) {
            result = result.replaceFirst("-", "");
        }

        return result;
    }

    private final ArrayList<String> argList;
    private final List<String> cleanArgs = new ArrayList<>();

    public StandardMain(final String[] args) {
        this.argList = new ArrayList<>(Arrays.asList(args));

        for (final String arg : argList) {
            cleanArgs.add(cleanArg(arg));
        }
    }

    @NonNull
    protected Optional<String> getArgValue(@NonNull final String argName) {
        return Util.tryNext(Util.getNext(cleanArgs, cleanArg(argName)), Util.getNext(argList, argName));
    }

    @NonNull
    protected String getRequiredArg(@NonNull final String argName) throws IllegalArgumentException {
        final Optional<String> value = getArgValue(argName);

        if (!value.isPresent()) {
            throw new IllegalArgumentException("Argument '" + argName + "' must be provided with a value, but was not.");
        }

        return value.get();
    }

    protected abstract void run() throws Exception;
}
