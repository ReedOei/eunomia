package com.reedoei.eunomia.latex;

import com.reedoei.eunomia.collections.ListUtil;
import com.reedoei.eunomia.collections.MapUtil;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

// TODO: addAverageRow
public class LatexTable {
    private final List<List<RationalCell>> cells = new ArrayList<>();

    private final Map<String, CellType> rowShow = new HashMap<>();
    private final Map<String, CellType> columnShow = new HashMap<>();

    private final List<String> columns;
    private final List<String> rows;

    private List<String> rowNames = new ArrayList<>();

    private boolean ignoreMissing = false;
    private boolean endRowsWithHline = true;
    private String replaceMissing = "";

    public LatexTable endRowsWithHline(final boolean endRowsWithHline) {
        this.endRowsWithHline = endRowsWithHline;
        return this;
    }

    // Rows/columns that are displayed as plain text
    // Key is the row/column index when they show be inserted
    private final Map<Integer, List<String>> latexRows = new HashMap<>();

    public List<String> rows() {
        return rows;
    }

    public List<String> columns() {
        return columns;
    }

    public LatexTable(final List<String> rows) {
        this(new ArrayList<>(), rows);
    }

    public LatexTable(final List<String> columns, final List<String> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public RationalCell getCell(String column, String row) {
        int x = columns.indexOf(column);
        int y = rows.indexOf(row);

        return cells.get(y).get(x);
    }

    public RationalCell getCell(CellPos cellPos) {
        switch (cellPos) {
            case BOTTOM_LEFT:
                return cells.get(cells.size() - 1).get(0);
            case TOP_RIGHT:
                return cells.get(0).get(cells.get(0).size() - 1);
            case BOTTOM_RIGHT:
                return cells.get(cells.size() - 1).get(cells.get(0).size() - 1);
            case TOP_LEFT:
            default:
                return cells.get(0).get(0);
        }
    }

    public LatexTable replaceMissingWith(String s) {
        this.replaceMissing = s;

        return this;
    }

    public LatexTable setupCell(String column, String row, String showOverride) {
        int x = columns.indexOf(column);
        int y = rows.indexOf(row);

        cells.get(y).get(x).showOverride = showOverride;
        return this;
    }

    public LatexTable setupCell(String column, String row, CellType display) {
        int x = columns.indexOf(column);
        int y = rows.indexOf(row);

        cells.get(y).get(x).cellType = display;

        return this;
    }

    public LatexTable setupCell(String column, String row, int value, int total) {
        int x = columns.indexOf(column);
        int y = rows.indexOf(row);

        cells.get(y).get(x).value = value;
        cells.get(y).get(x).total = total;

        return this;
    }

    public LatexTable setupCell(CellPos cellPos, CellType display) {
        getCell(cellPos).cellType = display;

        return this;
    }

    public LatexTable setupCell(CellPos cellPos, int value, int total) {
        getCell(cellPos).value = value;
        getCell(cellPos).total = total;

        return this;
    }

    public LatexTable setupCell(CellPos cellPos, boolean isHidden) {
        getCell(cellPos).isHidden = isHidden;

        return this;
    }

    public LatexTable ignoreMissing() {
        ignoreMissing = true;
        return this;
    }

    public LatexTable noIgnoreMissing() {
        ignoreMissing = false;
        return this;
    }

    public LatexTable setRowDisplay(final String rowName, final CellType val) {
        rowShow.put(rowName, val);

        return this;
    }

    public LatexTable setColumnDisplay(final String columnName, final CellType val) {
        columnShow.put(columnName, val);

        return this;
    }

    private Optional<RationalCell> makeCell(final @Nullable Integer value,
                                            final @Nullable Integer total,
                                            final CellType display) {
        if (value == null || total == null) {
            if (ignoreMissing) {
                return Optional.empty();
            } else if (replaceMissing != null && !replaceMissing.isEmpty()) {
                RationalCell rationalCell = new RationalCell(0, 0, display);
                rationalCell.showOverride = replaceMissing;
                return Optional.of(rationalCell);
            }
        } else {
            return Optional.of(new RationalCell(value, total, display));
        }

        return Optional.empty();
    }

    public LatexTable addRow(final Map<String, Integer> m, final Map<String, Integer> totals, CellType display) {
        final List<RationalCell> newRow = new ArrayList<>();

        for (final String column : columns) {
            makeCell(m.get(column), totals.getOrDefault(column, 0), display)
                    .ifPresent(newRow::add);
        }

        cells.add(newRow);

        return this;
    }

    public LatexTable addRow(final Map<String, Integer> m,
                             final CellType display) {
        final List<RationalCell> newRow = new ArrayList<>();
        setRowDisplay(rows.get(cells.size()), display);

        int total = MapUtil.total(m);

        for (final String column : columns) {
            makeCell(m.get(column), total, display)
                    .ifPresent(newRow::add);
        }

        cells.add(newRow);

        return this;
    }

    public LatexTable addLatexRow(final String latexString) {
        latexRows.compute(cells.size(), (i, rows) -> ListUtil.append(rows, latexString));

        return this;
    }

    private static BiFunction<String, Integer, Integer> incrementBy(final int amount) {
        return (ignored, count) -> count == null ? amount : count + amount;
    }

    public LatexTable addTotalRow(final String rowName, final CellType display) {
        return addTotalRow(rowName, display, false);
    }

    public LatexTable addTotalRow(final String rowName, final CellType display, final boolean useTotals) {
        final Map<String, Integer> values = new HashMap<>();
        final Map<String, Integer> totals = new HashMap<>();

        for (final String col : columns) {
            int c = columns.indexOf(col);
            for (int r = 0; r < rows.size(); r++) {
                values.compute(col, incrementBy(cells.get(r).get(c).value));

                if (useTotals) {
                    totals.compute(col, incrementBy(cells.get(r).get(c).total));
                } else {
                    totals.compute(col, incrementBy(cells.get(r).get(c).value));
                }
            }
        }

        rows.add(rowName);

        return addRow(values, totals, display);
    }

    public LatexTable addTotalColumn(final String colName, final CellType display) {
        for (final List<RationalCell> row : cells) {
            int total = row.stream().mapToInt(c -> c.value).sum();
            row.add(new RationalCell(total, 0, display));
        }

        columns.add(colName);

        return this;
    }

    public LatexTable addColumn(final String colName,
                                final Map<String, Integer> m,
                                final CellType display) {
        columns.add(colName);
//        setColumnDisplay(colName, display);

        int total = MapUtil.total(m);

        for (int i = 0; i < rows.size(); i++) {
            if (cells.size() <= i) {
                cells.add(new ArrayList<>());
            }

            int finalI = i;
            makeCell(m.get(rows.get(i)), total, display)
                    .ifPresent(rationalCell -> cells.get(finalI).add(rationalCell));
        }

        return this;
    }

    public LatexTable addColumn(final String colName,
                                final Map<String, Integer> m,
                                final Map<String, Integer> totals) {
        return addColumn(colName, m, totals, CellType.DEFAULT);
    }

    public LatexTable addColumn(String colName, Map<String, Integer> m, Map<String, Integer> totals, CellType display) {
        columns.add(colName);

        for (int i = 0; i < rows.size(); i++) {
            if (cells.size() <= i) {
                cells.add(new ArrayList<>());
            }

            int finalI = i;
            makeCell(m.get(rows.get(i)), totals.get(rows.get(i)), display)
                    .ifPresent(rationalCell -> cells.get(finalI).add(rationalCell));
        }

        return this;
    }

    public LatexTable setRowNames(List<String> rowNames) {
        this.rowNames = rowNames;
        return this;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean showRowNames) {
        final StringBuilder result = new StringBuilder();

        for (int y = 0; y < cells.size(); y++) {

            for (final String latexRow : latexRows.getOrDefault(y, Collections.emptyList())) {
                result.append(latexRow).append(System.lineSeparator());
            }

            List<RationalCell> row = cells.get(y);
            final List<String> cellStrs = new ArrayList<>();

            if (showRowNames) {
                if (rowNames.isEmpty()) {
                    cellStrs.add(rows.get(y));
                } else {
                    cellStrs.add(rowNames.get(y));
                }
            }

            for (int i = 0; i < row.size(); i++) {
                RationalCell rationalCell = row.get(i);

                int longestN = 0;
                int longestD = 0;
                int minLength = 0;
                int longestVal = 0;
                for (final List<RationalCell> r : cells) {
                    longestN = Math.max(longestN, Integer.toString(r.get(i).value).length());
                    if (r.get(i).cellType != CellType.VALUE && r.get(i).cellType != CellType.VALUE_SINGLE_COL) {
                        longestD = Math.max(longestD, Integer.toString(r.get(i).total).length());
                    }
                    minLength = Math.max(minLength, r.get(i).justRatio().length());
                    longestVal = Math.max(longestVal, Integer.toString(r.get(i).value).length());
                }

                if (rationalCell.cellType != CellType.DEFAULT) {
                    cellStrs.add(rationalCell.showAs(longestN, longestD, minLength, longestVal, rationalCell.cellType));
                } else {
                    final CellType rowType = rows.size() > y ? rowShow.getOrDefault(rows.get(y), CellType.DEFAULT) : CellType.DEFAULT;
                    final CellType colType = columns.size() > i ? columnShow.getOrDefault(columns.get(i), CellType.DEFAULT) : CellType.DEFAULT;

                    // Col type overrides row type unless it is default
                    if (colType == CellType.DEFAULT) {
                        cellStrs.add(rationalCell.showAs(longestN, longestD, minLength, longestVal, rowType));
                    } else {
                        cellStrs.add(rationalCell.showAs(longestN, longestD, minLength, longestVal, colType));
                    }
                }
            }

            result.append(String.join(" & ", cellStrs));
            if (endRowsWithHline) {
                result.append(" \\\\ \\hline");
            } else {
                result.append(" \\\\");
            }
            result.append(System.lineSeparator());
        }

        for (final String latexRow : latexRows.getOrDefault(cells.size(), Collections.emptyList())) {
            result.append(latexRow).append(System.lineSeparator());
        }

        return result.toString();
    }
}
