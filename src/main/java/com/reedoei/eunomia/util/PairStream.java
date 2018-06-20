package com.reedoei.eunomia.util;

import com.google.common.collect.Streams;
import com.reedoei.eunomia.functional.Pred;
import com.reedoei.eunomia.functional.TriConsumer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PairStream<T, U> {
    private Stream<Pair<T, U>> stream;

    public static <T,U> PairStream<T, U> zip(final Iterable<T> ts, final Iterable<U> us) {
        return new PairStream<>(ts, us);
    }

    public static <K, V> PairStream<@NonNull K, @NonNull V> fromMap(final Map<@NonNull K, V> m) {
        final List<@NonNull K> ks = new ArrayList<>(m.keySet());
        final List<@NonNull V> vs = ks.stream()
                .flatMap(MapUtil.getSafe(m))
                .collect(Collectors.toList());

        return PairStream.zip(ks, vs);
    }

    private PairStream(final Iterable<T> ts, final Iterable<U> us) {
        final Iterator<T> tIter = ts.iterator();
        final Iterator<U> uIter = us.iterator();

        stream = Streams.stream(new Iterator<Pair<T, U>>() {
            @Override
            public boolean hasNext() {
                return tIter.hasNext() && uIter.hasNext();
            }

            @Override
            public Pair<T, U> next() {
                return new ImmutablePair<>(tIter.next(), uIter.next());
            }
        });
    }

    private PairStream(final Stream<Pair<T, U>> stream) {
        this.stream = stream;
    }

    public PairStream<T, U> filter(final BiPredicate<T, U> f) {
        stream = stream.filter(p -> f.test(p.getLeft(), p.getRight()));

        return this;
    }

    public <V, W> PairStream<V, W> map(final BiFunction<T, U, Pair<V, W>> f) {
        return new PairStream<>(stream.map(p -> f.apply(p.getLeft(), p.getRight())));
    }

    public <V, W> PairStream<V, W> flatMap(final BiFunction<T, U, PairStream<V, W>> f) {
        return new PairStream<>(stream.flatMap(p -> f.apply(p.getLeft(), p.getRight()).stream));
    }

    public PairStream<T, U> distinct() {
        stream = stream.distinct();

        return this;
    }

    public PairStream<T, U> distinctFirst() {
        stream = stream.filter(Pred.nullSafe(Util.distinctByKey(Pair::getLeft)));

        return this;
    }

    public PairStream<T, U> distinctSecond() {
        stream = stream.filter(Pred.nullSafe(Util.distinctByKey(Pair::getRight)));

        return this;
    }

    public PairStream<T, U> sorted() {
        stream = stream.sorted();

        return this;
    }

    public PairStream<T, U> sorted(final Comparator<? super Pair<T, U>> comparator) {
        stream = stream.sorted(comparator);

        return this;
    }

    public PairStream<T, U> peek(final BiConsumer<T, U> consumer) {
        stream = stream.peek(p -> consumer.accept(p.getLeft(), p.getRight()));

        return this;
    }

    public PairStream<T, U> limit(long l) {
        stream = stream.limit(l);

        return this;
    }

    public PairStream<T, U> skip(long l) {
        stream = stream.skip(l);

        return this;
    }

    public void forEach(final BiConsumer<T, U> consumer) {
        stream.forEach(p -> consumer.accept(p.getLeft(), p.getRight()));
    }

    public void forEachOrdered(final BiConsumer<T, U> consumer) {
        stream.forEachOrdered(p -> consumer.accept(p.getLeft(), p.getRight()));
    }

    public Pair<T, U> reduce(final Pair<T, U> pair, final BinaryOperator<T> top, final BinaryOperator<U> uop) {
        return stream.reduce(pair, (a, b) -> Pair.of(top.apply(a.getLeft(), b.getLeft()), uop.apply(a.getRight(), b.getRight())));
    }

    public Optional<Pair<T, U>> reduce(final BinaryOperator<T> top, final BinaryOperator<U> uop) {
        return stream.reduce((a, b) -> Pair.of(top.apply(a.getLeft(), b.getLeft()), uop.apply(a.getRight(), b.getRight())));
    }

    public <V> V reduce(V v, BiFunction<V, Pair<T, U>, V> biFunction, final BinaryOperator<V> binaryOperator) {
        return stream.reduce(v, biFunction, binaryOperator);
    }

    public <R> R collect(final Supplier<R> supplier, final TriConsumer<R, T, U> consumer, BiConsumer<R, R> biConsumer) {
        return stream.collect(supplier, (r, p) -> consumer.accept(r, p.getLeft(), p.getRight()), biConsumer);
    }

    public <R, A> R collect(Collector<? super Pair<T, U>, A, R> collector) {
        return stream.collect(collector);
    }

    public Optional<Pair<T, U>> min(Comparator<? super Pair<T, U>> comparator) {
        return stream.min(comparator);
    }

    public Optional<Pair<T, U>> max(Comparator<? super Pair<T, U>> comparator) {
        return stream.min(comparator);
    }

    public long count() {
        return stream.count();
    }

    public boolean anyMatch(final BiPredicate<T, U> predicate) {
        return stream.anyMatch(p -> predicate.test(p.getLeft(), p.getRight()));
    }

    public boolean allMatch(final BiPredicate<T, U> predicate) {
        return stream.allMatch(p -> predicate.test(p.getLeft(), p.getRight()));
    }

    public boolean noneMatch(BiPredicate<T, U> predicate) {
        return stream.noneMatch(p -> predicate.test(p.getLeft(), p.getRight()));
    }

    public Optional<Pair<T, U>> findFirst() {
        return stream.findFirst();
    }

    public Optional<Pair<T, U>> findAny() {
        return stream.findAny();
    }

    @NonNull
    public Iterator<Pair<T, U>> iterator() {
        return stream.iterator();
    }
}
