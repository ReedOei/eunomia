package com.reedoei.eunomia.collections;

import com.google.common.collect.Streams;
import com.reedoei.eunomia.functional.ThrowingPredicate;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class StreamUtil {
    public static <T> Stream<T> removeEmpty(final Stream<Optional<T>> s) {
        return s.filter(Optional::isPresent).map(Optional::get);
    }

    /**
     * Force the evaluation of the stream (name inspired by Haskell).
     */
    public static <T> void seq(final Stream<T> stream) {
        stream.forEach(t -> {});
    }

    public static <T> Iterable<T> fromStream(final Stream<T> stream) {
        // Iterable is a functional interface, so because stream implements the right methods we can
        // do this.
        return stream::iterator;
    }

//    public static <T> Function<Stream<T>, Stream<T>> takeWhile(final ThrowingPredicate<T> pred) {
//        return s -> takeWhile(pred, s);
//    }
//
//    public static <T> Stream<T> takeWhile(final ThrowingPredicate<T> pred, final Stream<T> stream) {
//        return Streams.stream(new Iterator<T>() {
//            private boolean done = false;
//            private final Iterator<T> iterator = stream.iterator();
//
//            @Nullable
//            private T prev = null;
//
//            @SuppressWarnings("nullness")
//            @Override
//            public boolean hasNext() {
//                try {
//                    if (prev == null) {
//                        done = false;
//                        return false;
//                    } else {
//                        return done = iterator.hasNext() && pred.test(prev);
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            @SuppressWarnings("nullness")
//            @Override
//            public T next() {
//                prev = iterator.next();
//
//                try {
//                    if (prev == null || done) {
//                        return null;
//                    } else if (pred.test(prev)) {
//                        return prev;
//                    } else {
//                        done = true;
//                        return null;
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }
}
