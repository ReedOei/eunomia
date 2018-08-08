package com.reedoei.eunomia.util;

import com.reedoei.eunomia.collections.ListUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExecutionInfoTest {
    @Test
    public void testDefault() {
        final List<String> expected =
                ListUtil.fromArray("java", "-cp", System.getProperty("java.class.path"), ExecutionInfoTest.class.getCanonicalName());
        assertEquals(expected, new ExecutionInfoBuilder(ExecutionInfoTest.class).build().args());
    }
}