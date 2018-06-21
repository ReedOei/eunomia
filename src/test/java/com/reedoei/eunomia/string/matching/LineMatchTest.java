package com.reedoei.eunomia.string.matching;

import com.reedoei.eunomia.functional.Func;
import com.reedoei.eunomia.util.ListUtil;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class LineMatchTest {
    @Test
    public void testNextLine() {
        final List<String> lines = ListUtil.fromArray("A", "B", "C");

        final LineMatch match = new LineMatch(new WholeMatch("A"), lines, 0);
        final Optional<LineMatch> nextLine = match.nextLine();

        assertTrue(nextLine.isPresent());
        assertEquals("B", nextLine.get().get());
        assertEquals("TESTING", nextLine.get().map(Func.constant("TESTING")).get());
    }

    @Test
    public void testInsertBefore() {
        final List<String> lines = ListUtil.fromArray("A", "B", "C");

        final LineMatch match = new LineMatch(new WholeMatch("A"), lines, 0);
        final List<String> expected = ListUtil.fromArray("test", "A", "B", "C");
        assertEquals(expected, match.insertBefore("test").getLines());

        final Optional<LineMatch> nextLine = match.nextLine();
        assertTrue(nextLine.isPresent());
        assertEquals("B", nextLine.get().get());
    }

    @Test
    public void testInsertAfter() {
        final List<String> lines = ListUtil.fromArray("A", "B", "C");

        final LineMatch match = new LineMatch(new WholeMatch("A"), lines, 0);
        final List<String> expected = ListUtil.fromArray("A", "test", "B", "C");
        assertEquals(expected, match.insertAfter("test").getLines());
    }
}