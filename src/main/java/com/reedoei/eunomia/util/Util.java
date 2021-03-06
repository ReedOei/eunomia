package com.reedoei.eunomia.util;

import com.opencsv.CSVReader;
import com.reedoei.eunomia.collections.ListEx;
import com.reedoei.eunomia.collections.PairStream;
import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {
    public static ListEx<ListEx<String>> csv(final Path path) throws Exception {
        try (final FileInputStream fis = new FileInputStream(path.toAbsolutePath().toString());
             final InputStreamReader isr = new InputStreamReader(fis);
             final CSVReader reader = new CSVReader(isr)) {
            return new ListEx<>(reader.readAll()).map(ListEx::fromArray);
        }
    }

    public static <T> T until(final T t, final Function<T, T> f, final Predicate<T> pred) {
        if (pred.test(t)) {
            return t;
        } else {
            return until(f.apply(t), f, pred);
        }
    }

    public static <T> T untilNull(final T t, final Function<T, T> f) {
        return until(t, f, v -> f.apply(v) == null);
    }

    public static <T, U, V> BiFunction<List<T>, List<U>, List<V>> zipWith(final BiFunction<T, U, V> f) {
        return (ts, us) -> zipWith(f, ts, us);
    }

    public static <T, U, V> List<V> zipWith(final BiFunction<T, U, V> f,
                                            final List<T> ts,
                                            final List<U> us) {
        return PairStream.zip(ts, us).mapToStream(f).collect(Collectors.toList());
    }

    public static <T extends Comparable<? super T>> boolean inRange(final T t, final T min, final T max) {
        return t.compareTo(min) >= 0 && t.compareTo(max) <= -1;
    }

    public static <T> Optional<T> getNext(final List<T> ts, final @NonNull T t) {
        return getOffset(ts, t, 1);
    }

    public static <T> Optional<T> getPrevious(final List<T> ts, final @NonNull T t) {
        return getOffset(ts, t, -1);
    }

    public static <T> Optional<T> getOffset(final List<T> ts, final @NonNull T t, final int offset) {
        final int index = ts.indexOf(t);

        if (index != -1 && inRange(index + offset, 0, ts.size())) {
            return Optional.ofNullable(ts.get(index + offset));
        } else {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> tryNext( final Optional<T> a,  final Optional<T> b) {
        if (a.isPresent()) {
            return a;
        } else {
            return b;
        }
    }

    public static <T> Function<T, T> modify(final Consumer<T> f) {
        return base -> {
            f.accept(base);
            return base;
        };
    }

    public static <T> Function<List<T>, List<T>> prependAll( final List<T> toAdd) {
        return base -> {
            final List<T> result = Collections.synchronizedList(new ArrayList<>(base));
            result.addAll(0, toAdd);

            return result;
        };
    }

    public static <T> List<T> prependAll( final List<T> toAdd,  final List<T> ts) {
        return prependAll(toAdd).apply(ts);
    }

    public static <T> Function<List<T>, List<T>> appendAll(final List<T> toAdd) {
        return base -> {
            final List<T> result = Collections.synchronizedList(new ArrayList<>(base));
            result.addAll(toAdd);
            return result;
        };
    }

    public static <T> List<T> appendAll(final List<T> toAdd, final List<T> ts) {
        return appendAll(toAdd).apply(ts);
    }

    @Deprecated // In 1.4.1
    public static <T> List<T> topHalf(final List<T> ts) {
        return new ArrayList<>(ts.subList(0, ts.size() / 2));
    }

    @Deprecated // In 1.4.1
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

    public static Document readXmlDoc(final File xmlFile)
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

    public static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
        final Set<@Nullable Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
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

    public static <T> List<T> prepend(final T t, final List<T> rest) {
        return prependAll(Collections.singletonList(t), rest);
    }

    public static Function<Integer, Integer> incrementBy(final int amount) {
        return x -> x == null ? amount : x + amount;
    }
}
