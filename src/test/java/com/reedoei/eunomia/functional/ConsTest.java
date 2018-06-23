package com.reedoei.eunomia.functional;

import com.reedoei.eunomia.collections.ListUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class ConsTest {
    @Test
    public void onlyLeft() {
        final List<String> ls = new ArrayList<>();

        Cons.onlyLeft((Consumer<String>) ls::add).accept("test", "test3");

        assertEquals(ListUtil.fromArray("test"), ls);
    }

    @Test
    public void onlyRight() {
        final List<String> ls = new ArrayList<>();

        Cons.onlyRight((Consumer<String>) ls::add).accept("test", "test3");

        assertEquals(ListUtil.fromArray("test3"), ls);
    }

    @Test
    public void toBi() {
        final List<Integer> ls = new ArrayList<>();

        Cons.toBi((Pair<Integer, Integer> p) -> ls.add(p.getLeft() + p.getRight())).accept(-1, 2);

        assertEquals(ListUtil.fromArray(1), ls);
    }

    @Test
    public void fromBi() {
        List<String> ls = new ArrayList<>();
        Cons.fromBi((String a, String b) -> ls.add(a.replace(b, "%"))).accept(ImmutablePair.of("hello world!", "!"));

        assertEquals(ListUtil.fromArray("hello world%"), ls);
    }
}