package com.reedoei.eunomia.collections;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ListUtilTest {
    @Test
    public void concat() {
        assertEquals(ListUtil.fromArray(1, 2, 3),
                ListUtil.concat(ListUtil.fromArray(1, 2), ListUtil.fromArray(3)));
    }

    @Test
    public void reader() {
        assertEquals(ListUtil.fromArray(-6, 7, 122),
                ListUtil.reader(Integer::parseInt).apply("[-6, 7, 122]"));
    }

    @Test
    public void stringReader() {
        assertEquals(ListUtil.fromArray("A", "B", "K"),
                ListUtil.reader().apply("[A, B, K]"));
    }

    @Test
    public void read() {
        assertEquals(ListUtil.fromArray(1.5, 2.5, 3.5),
                ListUtil.read(Double::parseDouble, "[1.5, 2.5, 3.5]"));
    }

    @Test
    public void takeCurried() {
        assertEquals(ListUtil.fromArray(1, 2, 3),
                ListUtil.take(3).apply(ListUtil.fromArray(1, 2, 3, 4, 10)));
    }

    @Test
    public void take() {
        assertEquals(ListUtil.fromArray(1, 2, 3),
                ListUtil.take(3, ListUtil.range(1, 10)));
    }

    @Test
    public void takeWhileIncCurried() {
        assertEquals(ListUtil.fromArray(1, 2, 3),
                ListUtil.takeWhileInc((Integer i) -> i <= 2).apply(ListUtil.fromArray(1, 2, 3, 4)));
    }

    @Test
    public void takeWhileInc() {
        assertEquals(ListUtil.fromArray(1, 2, 3),
                ListUtil.takeWhileInc(i -> i <= 2, ListUtil.fromArray(1, 2, 3, 4)));
    }

    @Test
    public void takeWhileCurried() {
        assertEquals(ListUtil.fromArray(1, 2),
                ListUtil.takeWhile((Integer i) -> i <= 2).apply(ListUtil.fromArray(1, 2, 3, 4)));
    }

    @Test
    public void takeWhile() {
        assertEquals(ListUtil.fromArray(1, 2),
                ListUtil.takeWhile(i-> i <= 2, ListUtil.fromArray(1, 2, 3, 4)));
    }

    @Test
    public void dropWhileCurried() {
        assertEquals(ListUtil.fromArray(4, 5, 6),
                ListUtil.dropWhile((Integer i) -> i <= 3).apply(ListUtil.fromArray(1, 2, 3, 4, 5, 6)));
    }

    @Test
    public void dropWhile() {
        assertEquals(ListUtil.fromArray(4, 5, 6),
                ListUtil.dropWhile((Integer i) -> i <= 3, ListUtil.fromArray(1, 2, 3, 4, 5, 6)));
    }

    @Test
    public void dropCurried() {
        assertEquals(ListUtil.fromArray(8, 10, 12),
                ListUtil.drop(3).apply(ListUtil.fromArray(1, 2, 3, 8, 10, 12)));
    }

    @Test
    public void drop() {
        assertEquals(ListUtil.fromArray(8, 10, 12),
                ListUtil.drop(3, ListUtil.fromArray(1, 2, 3, 8, 10, 12)));
    }

    @Test
    public void fromArray() {
        assertEquals(ListUtil.fromArray(1, 2), ListUtil.fromArray(null, 1, 2));
    }

    @Test
    public void fromArrayUnsafe() {
        final List<@Nullable Integer> xs = new ArrayList<>();
        xs.add(null);
        xs.add(1);
        xs.add(2);

        assertEquals(xs, ListUtil.fromArrayUnsafe(null, 1, 2));
    }

    @Test
    public void subsequences() {
        System.out.println(ListUtil.subsequences(ListUtil.fromArray(1, 2, 3)).collect(Collectors.toList()));
    }

    @Test
    public void rangeJustEnd() {
        assertEquals(ListUtil.fromArray(0, 1, 2), ListUtil.range(3));
    }

    @Test
    public void rangeStartAndEnd() {
        assertEquals(ListUtil.fromArray(1, 2), ListUtil.range(1, 3));
    }

    @Test
    public void range() {
        assertEquals(ListUtil.fromArray(1, 3, 5, 7), ListUtil.range(1, 8, 2));
    }
}