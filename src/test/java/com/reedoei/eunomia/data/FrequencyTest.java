package com.reedoei.eunomia.data;

import com.reedoei.eunomia.collections.ListUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

public class FrequencyTest {
    @SuppressWarnings("nullness")
    private Frequency<String> frequency;

    @Before
    public void setUp() {
        frequency = Frequency.initWith(ListUtil.fromArray("A", "B", "C"));

        ListUtil.fromArray("A", "A", "A", "B", "C", "C").forEach(frequency::count);
    }

    @Test
    public void initWith() {
        frequency = Frequency.initWith(ListUtil.fromArray("A", "B", "C"));

        assertEquals(ListUtil.fromArray("A", "B", "C"), ListUtil.collect(frequency));
    }

    @Test
    public void countSingle() {
        frequency.count("A");

        assertEquals(Optional.of(4), frequency.get("A"));
    }

    @Test
    public void countDefaultIncrement() {
        frequency.count("STRING WITH B IN IT", (key, t) -> t.contains(key));

        assertEquals(Optional.of(2), frequency.get("B"));
    }

    @Test
    public void count() {
        frequency.count("STRING WITH B IN IT", (key, t) -> t.contains(key), (k, v) -> (v+7)*v*v);

        assertEquals(Optional.of(8), frequency.get("B"));
    }

    @Test
    public void put() {
        frequency.put("D", 1004);

        assertEquals(Optional.of(1004), frequency.get("D"));
    }

    @Test
    public void map() {
        final Frequency<String> newFreq = frequency.map(v -> v * v);

        assertEquals(Optional.of(9), newFreq.get("A"));
    }

    @Test
    public void mapWithKey() {
        final Frequency<String> newFreq = frequency.map((k, v) -> ((int)k.charAt(0)) * v * v);

        assertEquals(Optional.of(585), newFreq.get("A"));
    }

    @Test
    public void filter() {
        final Frequency<String> newFreq = frequency.filter(v -> v > 1);

        assertThat(ListUtil.collect(newFreq), hasItems("A", "C"));
        assertEquals(2, newFreq.size());
    }

    @Test
    public void filterWithKey() {
        final Frequency<String> newFreq = frequency.filter((k, v) -> k.equals("B") || v > 2);

        assertThat(ListUtil.collect(newFreq), hasItems("A", "B"));
        assertEquals(2, newFreq.size());
    }

    @Test
    public void forEach() {
        final List<String> temp = new ArrayList<>();

        frequency.forEach((k, v) -> temp.add(k + ": " + v));

        assertThat(temp, hasItems("A: 3", "B: 1", "C: 2"));
    }

    @Test
    public void max() {
        assertEquals(Optional.of("A"), frequency.max());
    }

    @Test
    public void min() {
        assertEquals(Optional.of("B"), frequency.min());
    }
}