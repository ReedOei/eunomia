package com.reedoei.eunomia.util;

import com.google.common.base.Preconditions;
import com.reedoei.eunomia.collections.StreamUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class StandardMain {
    private static String cleanArg(@NonNull final String arg) {
        String result = arg;
        while (result.startsWith("-")) {
            result = result.replaceFirst("-", "");
        }

        return result;
    }

    protected final ArrayList<String> argList;
    private final List<String> cleanArgs = new ArrayList<>();

    public StandardMain(final String[] args) {
        this.argList = new ArrayList<>(Arrays.asList(args));

        for (final String arg : argList) {
            cleanArgs.add(cleanArg(arg));
        }
    }

    public Optional<String> getArg(final String... argNames) {
        return StreamUtil.removeEmpty(Arrays.stream(argNames).map(this::getArg)).findFirst();
    }

    @NonNull
    public Optional<String> getArg(@NonNull final String argName) {
        return Util.tryNext(Util.getNext(cleanArgs, cleanArg(argName)), Util.getNext(argList, argName));
    }

    @NonNull
    public String getArgRequired(@NonNull final String argName) {
        return getArgRequired(new String[]{argName});
    }

    public String getArgRequired(final String... argNames) {
        final Optional<String> value = getArg(argNames);

        if (!value.isPresent()) {
            throw new IllegalArgumentException("You must pass in one of '" + Arrays.toString(argNames) + "' with a value!");
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
