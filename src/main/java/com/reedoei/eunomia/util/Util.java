package com.reedoei.eunomia.util;

import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {
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

    public static <K> Set<K> findInAll(final Set<K>... sets) {
        final Set<K> result = new HashSet<>();

        for (final Set<K> set : sets) {
            for (final K k : set) {
                if (Arrays.stream(sets).allMatch(s -> s.contains(k))) {
                    result.add(k);
                }
            }
        }

        return result;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static <K> Map<K, Integer> initFreqMap(final Iterable<K> keys) {
        final Map<K, Integer> result = new HashMap<>();

        for (final K k : keys) {
            result.put(k, 0);
        }

        return result;
    }

    public static <K> Optional<K> maxKey(final Map<K, Integer> map) {
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
}
