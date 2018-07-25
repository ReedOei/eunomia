package com.reedoei.eunomia.collections;

import java.util.function.Function;
import java.util.stream.Stream;

public class Products {
    public static void test() {
        apply(apply(apply(Stream.of(t -> u -> v -> t + u + v), Stream.of(1)), Stream.of(2)), Stream.of(5));
    }

    public static <A, B> Stream<B> apply(final Stream<Function<A, B>> fs, final Stream<A> as) {
        return PairStream.product(fs::iterator, as::iterator).mapToStream(Function::apply);
    }

    public static <A, B, C> Stream<C> withAll(final Stream<Function<A, Function<B, C>>> fs, final Stream<A> as, final Stream<B> bs) {
        return apply(apply(fs, as), bs);
    }

    public static <A, B, C, D> Stream<D> withAll(final Stream<Function<A, Function<B, Function<C, D>>>> fs,
                                                 final Stream<A> as,
                                                 final Stream<B> bs,
                                                 final Stream<C> cs) {
        return apply(apply(apply(fs, as), bs), cs);
    }

    public static <A, B, C, D, E> Stream<E> withAll(final Stream<Function<A, Function<B, Function<C, Function<D, E>>>>> fs,
                                                    final Stream<A> as,
                                                    final Stream<B> bs,
                                                    final Stream<C> cs,
                                                    final Stream<D> ds) {
        return apply(apply(apply(apply(fs, as), bs), cs), ds);
    }

    public static <A, B, C, D, E, F> Stream<F> withAll(final Stream<Function<A, Function<B, Function<C, Function<D, Function<E, F>>>>>> fs,
                                                       final Stream<A> as,
                                                       final Stream<B> bs,
                                                       final Stream<C> cs,
                                                       final Stream<D> ds,
                                                       final Stream<E> es) {
        return apply(apply(apply(apply(apply(fs, as), bs), cs), ds), es);
    }

    public static <A, B, C, D, E, F, G> Stream<G> withAll(
            final Stream<Function<A, Function<B, Function<C, Function<D, Function<E, Function<F, G>>>>>>> fs,
            final Stream<A> as,
            final Stream<B> bs,
            final Stream<C> cs,
            final Stream<D> ds,
            final Stream<E> es,
            final Stream<F> fStream) {
        return apply(apply(apply(apply(apply(apply(fs, as), bs), cs), ds), es), fStream);
    }

    public static <A, B, C, D, E, F, G, H> Stream<H> withAll(
            final Stream<Function<A, Function<B, Function<C, Function<D, Function<E, Function<F, Function<G, H>>>>>>>> fs,
            final Stream<A> as,
            final Stream<B> bs,
            final Stream<C> cs,
            final Stream<D> ds,
            final Stream<E> es,
            final Stream<F> fStream,
            final Stream<G> gs) {
        return apply(apply(apply(apply(apply(apply(apply(fs, as), bs), cs), ds), es), fStream), gs);
    }
}
