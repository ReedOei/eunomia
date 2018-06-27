package com.reedoei.eunomia.functional;

import java.util.Optional;

// TODO: Write tests.
public class Either<A, B> {
    public static <A, B> Either<A, B> left(final A a) {
        return new Either<>(Optional.ofNullable(a), Optional.empty());
    }

    public static <A, B> Either<A, B> right(final B b) {
        return new Either<>(Optional.empty(), Optional.ofNullable(b));
    }

    private final Optional<A> a;
    private final Optional<B> b;

    public Either(final Optional<A> a, final Optional<B> b) {
        this.a = a;
        this.b = b;
    }

    public A getLeft() {
        return a.orElseThrow(() -> new IllegalArgumentException("Cannot get left value because it does not exist!"));
    }

    public B getRight() {
        return b.orElseThrow(() -> new IllegalArgumentException("Cannot get right value because it does not exist!"));
    }
}
