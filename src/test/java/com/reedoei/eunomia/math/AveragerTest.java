package com.reedoei.eunomia.math;

import com.reedoei.eunomia.util.ListUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AveragerTest {
    private static final List<Double> values = ListUtil.fromArray(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);

    @SuppressWarnings("nullness")
    private Averager averager;

    @Before
    public void setUp() throws Exception {
        averager = new Averager(values);
    }

    @Test
    public void add() {
        averager.add(11.0);
        assertEquals(6.0, averager.mean(), 0.000001);
    }

    @Test
    public void addAll() {
        final List<Double> moreValues = ListUtil.fromArray(-1.0, 5.0, 112.0);
        averager.addAll(moreValues);

        assertEquals(13.153846153846153, averager.mean(), 0.0000001);
    }

    @Test
    public void arithMean() {
        assertEquals(5.5, averager.arithMean(), 0.0000001);
    }

    @Test
    public void mean() {
        assertEquals(5.5, averager.mean(), 0.0000001);
    }

    @Test
    public void geoMean() {
        assertEquals(4.528728688116765, averager.geoMean(), 0.0000001);
    }
}