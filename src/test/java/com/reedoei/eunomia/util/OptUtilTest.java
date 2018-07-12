package com.reedoei.eunomia.util;

import com.reedoei.eunomia.collections.ListUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class OptUtilTest {
    @Test
    public void ifAllPresent2Arg() {
        final List<String> x = new ArrayList<>();

        OptUtil.ifAllPresent(Optional.of("Hi"), Optional.of("Cats"), (a, b) -> {
            x.add(a);
            x.add(b);
        });

        assertEquals(ListUtil.fromArray("Hi", "Cats"), x);
    }

    @Test
    public void ifAllPresent2ArgNotAllPresent() {
        final List<String> x = new ArrayList<>();

        OptUtil.ifAllPresent(Optional.of("Hi"), Optional.empty(), (a, b) -> x.add(a));

        assertEquals(0, x.size());
    }

    @Test
    public void ifAllPresent3Arg() {
        final List<String> x = new ArrayList<>();

        OptUtil.ifAllPresent(Optional.of("Hi"), Optional.of("Cats"), Optional.of("Testing"), (a, b, c) -> {
            x.add(a);
            x.add(b);
            x.add(c);
        });

        assertEquals(ListUtil.fromArray("Hi", "Cats", "Testing"), x);
    }

    @Test
    public void ifAllPresent3ArgNotAllPresent() {
        final List<String> x = new ArrayList<>();

        OptUtil.ifAllPresent(Optional.empty(), Optional.of("Cats"), Optional.of("Testing"), (a, b, c) -> {
            x.add(String.valueOf(a));
            x.add(b);
            x.add(c);
        });

        assertEquals(0, x.size());
    }

    @Test
    public void map2Arg() {
        final Optional<Double> v = OptUtil.map(Optional.of(3.0), Optional.of(5.0), (a, b) -> a * b);
        assertEquals(Optional.of(15.0), v);
    }

    @Test
    public void map2ArgNotAllPresent() {
        final Optional<Double> v = OptUtil.<Double, Double, Double>map(Optional.empty(), Optional.of(5.0), (a, b) -> a * b);
        assertEquals(Optional.empty(), v);
    }

    @Test
    public void map3Arg() {
        final Optional<Double> v = OptUtil.map(Optional.of(3.0), Optional.of(5.0), Optional.of(4.5), (a, b, c) -> a * b - c);
        assertEquals(Optional.of(10.5), v);
    }

    @Test
    public void map3ArgNotAllPresent() {
        final Optional<Double> v = OptUtil.<Double, Double, Double, Double>map(Optional.of(3.0), Optional.of(5.0), Optional.empty(), (a, b, c) -> a * b - c);
        assertEquals(Optional.empty(), v);
    }
}