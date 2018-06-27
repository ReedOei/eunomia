package com.reedoei.eunomia.util;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    public Optional<String> getArg(@NonNull final String argName) {
        return Util.tryNext(Util.getNext(cleanArgs, cleanArg(argName)), Util.getNext(argList, argName));
    }

    @NonNull
    public String getArgRequired(@NonNull final String argName) {
        final Optional<String> value = getArg(argName);

        if (!value.isPresent()) {
            throw new IllegalArgumentException("You must pass in argument '" + argName + "' with a value!");
        }

        return value.get();
    }

    @Deprecated
    @NonNull
    protected Optional<String> getArgValue(@NonNull final String argName) {
        return getArg(argName);
    }

    @Deprecated
    @NonNull
    protected String getRequiredArg(@NonNull final String argName) throws IllegalArgumentException {
        return getArgRequired(argName);
    }

    protected abstract void run() throws Exception;
}
