package com.reedoei.eunomia.util;

import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {
    public static <T extends Comparable<? super T>> boolean inRange(@NotNull final T t,
                                                                    @NotNull final T min,
                                                                    @NotNull final T max) {
        return t.compareTo(min) >= 0 &&
                t.compareTo(max) <= -1;
    }

    @NotNull
    public static <T> Optional<T> getNext(@NotNull final List<T> ts, final T t) {
        return getOffset(ts, t, 1);
    }

    @NotNull
    public static <T> Optional<T> getPrevious(@NotNull final List<T> ts, final T t) {
        return getOffset(ts, t, -1);
    }

    @NotNull
    public static <T> Optional<T> getOffset(@NotNull final List<T> ts, final T t, final int offset) {
        final int index = ts.indexOf(t);

        if (inRange(index, 0, ts.size())) {
            return Optional.ofNullable(ts.get(index + offset));
        } else {
            return Optional.empty();
        }
    }

    @NotNull
    public static <T> Optional<T> tryNext(@NotNull final Optional<T> a, @NotNull final Optional<T> b) {
        if (a.isPresent()) {
            return a;
        } else {
            return b;
        }
    }

    public static <T> List<T> takeWhileInc(final Predicate<T> f, final List<T> ts) {
        final List<T> result = new ArrayList<>();

        for (final T t : ts) {
            result.add(t);
            if (!f.test(t)) {
                break;
            }
        }

        return result;
    }

    public static <T> List<T> beforeInc(final List<T> ts, final T t) {
        final int i = ts.indexOf(t);

        if (i != -1) {
            return new ArrayList<>(ts);
        } else {
            return new ArrayList<>();
        }
    }

    public static <T> Function<List<T>, List<T>> modify(final Consumer<List<T>> f) {
        return base -> {
            f.accept(base);
            return base;
        };
    }

    public static <T> Function<List<T>, List<T>> prependAll(final List<T> toAdd) {
        return modify(base -> base.addAll(0, toAdd));
    }

    public static <T> List<T> prependAll(final List<T> toAdd, final List<T> ts) {
        return prependAll(toAdd).apply(ts);
    }

    public static <T> Function<List<T>, List<T>> appendAll(final List<T> toAdd) {
        return modify(base -> base.addAll(toAdd));
    }

    public static <T> List<T> appendAll(final List<T> toAdd, final List<T> ts) {
        return appendAll(toAdd).apply(ts);
    }

    public static <T> List<T> topHalf(final List<T> ts) {
        return new ArrayList<>(ts.subList(0, ts.size() / 2));
    }

    public static <T> List<T> botHalf(final List<T> ts) {
        return new ArrayList<>(ts.subList(ts.size() / 2, ts.size()));
    }

    public static <K, T> Map<K, List<T>> groupBy(final List<T> list, final Function<T, K> f) {
        final Map<K, List<T>> result = new HashMap<>();

        for (final T t : list) {
            result.compute(f.apply(t),
                    (key, xs) -> {
                        if (xs == null) {
                            xs = new ArrayList<>();
                        }

                        xs.add(t);

                        return xs;
                    });
        }

        return result;
    }

    public static <K,V> Optional<Map.Entry<K, V>> where(final Map<K, V> map, BiPredicate<K, V> pred) {
        return map.entrySet().stream()
                .filter(entry -> pred.test(entry.getKey(), entry.getValue()))
                .findAny();
    }

    public static String elementToString(final Element element) {
        StringWriter sWriter = new StringWriter();

        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(sWriter, format);
        try {
            writer.write(element);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sWriter.flush();
        return sWriter.toString();
    }

    public static int total(final Map<String, Integer> map) {
        return map.values().stream().mapToInt(Integer::intValue).sum();
    }

    public static int count(final Map<String, Boolean> map) {
        return count(map, (k, v) -> v);
    }

    public static <K, V> int count(final Map<K, V> map, final BiPredicate<K, V> pred) {
        return Math.toIntExact(map.entrySet().stream()
                .filter(entry -> pred.test(entry.getKey(), entry.getValue()))
                .count());
    }

    public static BiFunction<String, Integer, Integer> incrementBy(final int amount) {
        return (ignored, count) -> count == null ? amount : count + amount;
    }

    public static <T> Set<T> addSet(Set<T> a, final Set<T> b) {
        if (a == null) {
            a = new HashSet<>();
        }

        a.addAll(b);
        return a;
    }

    public static <T> Set<T> wrapSet(T a) {
        return new HashSet<>(Collections.singleton(a));
    }

    public static <T> Map<String, Set<T>> parseDirToSetMap(final Optional<String> argValue,
                                                           final Function<Document, T> constructor,
                                                           final Function<T, String> getId) {
        return argValue
                .map(dir -> parseFromDir(new File(dir), constructor))
                .orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(getId, Util::wrapSet, Util::addSet));
    }

    public static <T> Set<T> parseDirToSet(final Optional<String> argValue,
                                           final Function<Document, Set<T>> constructor) {
        return argValue
                .map(dir -> parseFromDir(new File(dir), constructor))
                .orElse(new ArrayList<>()).stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public static <T> Set<T> parseToSet(final Optional<String> argValue,
                                        final Function<Document, Set<T>> constructor) {
        return argValue
                .flatMap(file -> parseFromFile(new File(file), constructor))
                .orElse(new HashSet<>());
    }

    private static Document readXmlDoc(final File xmlFile)
            throws MalformedURLException, DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(xmlFile.toURI().toURL());
    }

    public static <T> Map<String, T> parseDir(final Optional<String> argValue,
                                              final Function<Document, T> constructor,
                                              final Function<T, String> getId) {
        return argValue
                .map(dir -> Util.parseFromDir(new File(dir), constructor))
                .orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(getId, Function.identity(), (a, b) -> a));
    }

    public static <T> Optional<T> parseFromFile(final File file,
                                                final Function<Document, T> constructor) {
        try {
            return Optional.ofNullable(constructor.apply(readXmlDoc(file)));
        } catch (IOException | DocumentException e) {
            System.out.println("An error occurred while loading the XML documents: ");
            e.printStackTrace();
            System.out.println("Exiting.");

            System.exit(0);
        }

        return Optional.empty();
    }

    public static <T> List<T> parseFromDir(final File dir,
                                           final Function<Document, T> constructor) {
        final List<T> result = new ArrayList<>();

        try {
            final File[] files = dir.listFiles();
            // Could be null if it's not a directory or if an error occurs while opening it.
            if (files != null) {
                for (final File file : files) {
                    if (FilenameUtils.getExtension(file.getCanonicalPath()).equals("xml")) {
                        final Document doc = readXmlDoc(file);
                        result.add(constructor.apply(doc));
                    }
                }
            }
        } catch (IOException | DocumentException e) {
            System.out.println("An error occurred while loading the XML documents: ");
            e.printStackTrace();
            System.out.println("Exiting.");

            System.exit(0);
        }

        return result;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static <K, V extends Comparable<? super V>> Optional<K> maxKey(final Map<K, V> map) {
        return map.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    public static <K, V> BiFunction<K, Set<V>, Set<V>> add(final V v) {
        return (k, vs) -> {
            if (vs == null) {
                vs = new HashSet<>();
            }

            vs.add(v);
            return vs;
        };
    }

    public static <T> Set<T> common(final Set<T>... sets) {
        if (sets.length == 0) {
            return new HashSet<>();
        }

        final Set<T> result = new HashSet<>();

        // We only have to loop through the first set, because values in all sets must be in the
        // first set
        for (final T t : sets[0]) {
            if (result.contains(t)) {
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
