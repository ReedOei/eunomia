package com.reedoei.eunomia.string;

import com.reedoei.eunomia.string.matching.LineMatch;
import com.reedoei.eunomia.string.searching.Searcher;
import com.reedoei.eunomia.string.searching.StringSearch;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class StringSearchTest {
    private static final String SEARCH_STRING =
            "Value:" + System.lineSeparator() +
            "1" + System.lineSeparator() +
            "Testing" + System.lineSeparator() +
            "Time: 40" + System.lineSeparator();

    @Test
    public void testSearch() throws Exception {
        final StringSearch search = new StringSearch(SEARCH_STRING);

        assertEquals(1, search.search(Searcher.exactly("Value:")).count());
        final Optional<Integer> result = search.search(Searcher.exactly("Value:"))
                .map(Context.applyNextLine(Integer::parseInt))
                .findFirst();

        assertTrue(result.isPresent());
        assertEquals(1, result.get().intValue());
    }

    @Test
    public void testSearchContains() throws Exception {
        final StringSearch search = new StringSearch(SEARCH_STRING);

        assertEquals(1, search.search(Searcher.contains("Time: ")).count());
        final Optional<Double> result = search.search(Searcher.contains("Time: "))
                .map(LineMatch::nonmatching)
                .map(Double::parseDouble)
                .findAny();

        assertTrue(result.isPresent());
        assertEquals(40, result.get(), 0.0000000001);
    }
}
