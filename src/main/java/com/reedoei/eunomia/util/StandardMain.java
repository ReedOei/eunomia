package com.reedoei.eunomia.util;

import com.reedoei.eunomia.collections.StreamUtil;
import com.reedoei.eunomia.subject.classpath.Classpath;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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

    protected final ArrayList<String> argList;
    private final List<String> cleanArgs = new ArrayList<>();

    public StandardMain(final String[] args) {
        this.argList = new ArrayList<>(Arrays.asList(args));

        for (final String arg : argList) {
            cleanArgs.add(cleanArg(arg));
        }
    }

    public ArrayList<String> argList() {
        return argList;
    }

    public List<String> cleanArgs() {
        return cleanArgs;
    }

    public boolean hasArg(final String... argNames) {
        for (final String argName : argNames) {
           if (cleanArgs.contains(cleanArg(argName)) || argList.contains(argName)) {
               return true;
           }
        }

        return false;
    }

    public String getClasspathArg() {
        return Classpath.build(getArg("cp", "classpath").orElse(System.getProperty("java.class.path"))).toString();
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

    protected abstract void run() throws Exception;
}
