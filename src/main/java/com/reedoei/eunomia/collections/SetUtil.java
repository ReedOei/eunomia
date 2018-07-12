package com.reedoei.eunomia.collections;

import com.reedoei.eunomia.functional.Func;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SetUtil {
    public static <T, U> Function<Set<T>, Set<U>> map(final Function<T, U> f) {
        return l -> map(f, l);
    }

    public static <T, U> Set<U> map(final Function<T, U> f, final Set<T> ts) {
        return ts.stream().map(f).collect(Collectors.toSet());
    }

    public static <T> Function<Set<T>, Set<T>> filter(final Predicate<T> pred) {
        return l -> filter(pred, l);
    }

    public static <T> Set<T> filter(final Predicate<T> pred, final Set<T> ts) {
        return ts.stream().filter(pred).collect(Collectors.toSet());
    }

    public static Function<String, Set<String>> reader() {
        return reader(Function.identity());
    }

    public static <T> Function<String, Set<T>> reader(final Function<String, T> f) {
        return s -> read(f, s);
    }

    public static Set<String> read(final String s) {
        return read(Function.identity(), s);
    }

    public static <T> Set<T> read(final Function<String, T> f, final String s) {
        return Collections.synchronizedSet(new HashSet<>(ListUtil.read(s, f)));
    }

    @SafeVarargs
    public static <T> Set<T> intersect(final Set<T>... sets) {
        final Set<T> result = Collections.synchronizedSet(new HashSet<>());

        if (sets.length == 0) {
            return result;
        }

        // We only have to loop through the first set, because values in all sets must be in the
        // first set
        for (final T t : sets[0]) {
            if (t != null && result.contains(t)) {
                continue;
            }

            if (t == null) {
                continue;
            }

            boolean allContain = true;

            for (final Set<T> set : sets) {
                if (!set.contains(t)) {
                    allContain = false;
                    break;
                }
            }

            if (allContain) {
                result.add(t);
            }
        }

        return result;
    }
}
