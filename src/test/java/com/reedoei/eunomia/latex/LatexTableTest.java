package com.reedoei.eunomia.latex;

import com.reedoei.eunomia.collections.ListUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class LatexTableTest {
    final LatexTable table = new LatexTable(ListUtil.fromArray("A", "B", "Testing column"), ListUtil.fromArray("first", "second"));
    private static final Map<String, Integer> firstRow = new HashMap<>();
    private static final Map<String, Integer> secondRow = new HashMap<>();

    @Before
    public void setUp() {
        firstRow.put("A", 1);
        firstRow.put("B", 4);
        firstRow.put("Testing column", 0);

        secondRow.put("A", 8);
        secondRow.put("B", 9);
        secondRow.put("Testing column", 122);
    }

    @After
    public void tearDown() {
        firstRow.clear();
        secondRow.clear();
    }

    @Test
    public void testJustPercent() {
        table.addRow(firstRow, CellType.JUST_PERCENT);

        assertEquals("first & 20.0\\% & 80.0\\% & 0.0\\% \\\\ \\hline\n", table.toString());
    }

    @Test
    public void testStandardTable() {
        table.addRow(firstRow, CellType.VALUE_SINGLE_COL);
        table.addRow(secondRow, CellType.VALUE_SINGLE_COL);

        assertEquals("first & 1 & 4 & \\z\\z0 \\\\ \\hline\n" +
                "second & 8 & 9 & 122 \\\\ \\hline\n", table.toString());
    }

    @Test
    public void testTableWithTotalColumn() {
        table.addRow(firstRow, CellType.PERCENT);
        table.addRow(secondRow, CellType.PERCENT);

        table.addTotalColumn("Total", CellType.VALUE_SINGLE_COL);

        assertEquals("first & 20.0\\% & (1/\\z\\z5) & 80.0\\% & (4/\\z\\z5) & \\z0.0\\% & (\\z\\z0/\\z\\z5) & \\z\\z5 \\\\ \\hline\n" +
                "second & \\z5.8\\% & (8/139) & \\z6.5\\% & (9/139) & 87.8\\% & (122/139) & 139 \\\\ \\hline\n", table.toString());
    }

    @Test
    public void testTableWithTotalRow() {
        table.addRow(firstRow, CellType.RATIO);
        table.addRow(secondRow, CellType.RATIO);

        table.addTotalRow("Total row", CellType.VALUE);

        assertEquals("first & 0.2 & (1/\\z\\z5) & 0.8 & (\\z4/\\z\\z5) & 0.0 & (\\z\\z0/\\z\\z5) \\\\ \\hline\n" +
                "second & 0.1 & (8/139) & 0.1 & (\\z9/139) & 0.9 & (122/139) \\\\ \\hline\n" +
                "Total row & & 9 & & 13 & & 122 \\\\ \\hline\n", table.toString());
    }

    @Test
    public void testTableWithLatexStringRow() {
        table.addLatexRow("Testing row that is plain text at the beginning\\\\");
        table.addRow(firstRow, CellType.RATIO);
        table.addLatexRow("Testing row that is plain text in between rows \\\\");
        table.addRow(secondRow, CellType.RATIO);

        table.addLatexRow("Testing row that is plain text at the end\\\\");

        assertEquals("Testing row that is plain text at the beginning\\\\\n" +
                "first & 0.2 & (1/\\z\\z5) & 0.8 & (4/\\z\\z5) & 0.0 & (\\z\\z0/\\z\\z5) \\\\ \\hline\n" +
                "Testing row that is plain text in between rows \\\\\n" +
                "second & 0.1 & (8/139) & 0.1 & (9/139) & 0.9 & (122/139) \\\\ \\hline\n" +
                "Testing row that is plain text at the end\\\\\n", table.toString());
    }
}