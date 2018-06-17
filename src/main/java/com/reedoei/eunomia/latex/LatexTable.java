package com.reedoei.eunomia.latex;

import com.reedoei.eunomia.util.Util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class LatexTable {
    public static class Cell {
        public int value;
        public int total;
        public CellType cellType;
        public boolean isHidden = false;
        public String showOverride = null;

        public Cell(int value, int total, CellType type) {
            this.value = value;
            this.total = total;
            this.cellType = type;
        }

        public Cell(int value, int total, CellType type, boolean isHidden) {
            this.value = value;
            this.total = total;
            this.cellType = type;
            this.isHidden = isHidden;
        }

        public String justPercent() {
            return LatexTable.justPercent(value, total, false);
        }

        public String justRatio() {
            return LatexTable.justRatio(value, total, false);
        }

        public String percent(int longestN, int longestD, String showDenominator) {
            return LatexTable.percent(value, total, longestN, longestD, showDenominator, showOverride);
        }

        public String ratio(int longestN, int longestD, int minLength, String showDenominator) {
            return LatexTable.ratio(value, total, longestN, longestD, minLength, showDenominator);
        }

        public String showAs(int longestN, int longestD, int minLength, int longestVal, CellType cellType) {
            if (isHidden) {
                if (cellType == CellType.VALUE_SINGLE_COL) {
                    return "";
                } else {
                    return "&";
                }
            }
//
//            if (showOverride != null && !showOverride.isEmpty()) {
//                return showOverride;
//            }

            switch (cellType) {
                case PERCENT:
                    return percent(longestN, longestD, null);
                case PERCENT_NO_DENOM:
                    return percent(longestN, longestD, "");
                case RATIO:
                    return ratio(longestN, longestD, minLength, null);
                case VALUE:
                    if (showOverride == null || showOverride.isEmpty()) {
                        return "& " + latexPad(value, longestVal);
                    } else {
                        return "& " + showOverride;
                    }

                case VALUE_SINGLE_COL:
                    return latexPad(value, longestVal);
                case DEFAULT:
                    return percent(longestN, longestD, null);
            }

            return "Unhandled cell type: " + cellType;
        }

        public int getValue() {
            return value;
        }
    }

    public static <T> String latexPad(T value, int i) {
        return latexPad(value.toString(), i);
    }

    public static String latexPad(String value, int i) {
        final StringBuilder res = new StringBuilder(value);

        int needed = i - value.length();

        for (int j = 0; j < needed; j++) {
            res.insert(0, "\\z");
        }

        return res.toString();
    }

    public static String justPercent(int val, int total, boolean showEquals) {
        final NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMinimumFractionDigits(1);
        percent.setMaximumFractionDigits(1);

        if (showEquals) {
            return val + "/" + total + " = " + percent.format((double)val / (double)total);
        } else {
            return percent.format((double)val / (double)total).replace("%", "\\%");
        }
    }

    public static String percent(int val, int total, int longestN, int longestD, String showDenominator, String showOverride) {
        final NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMinimumFractionDigits(1);
        percent.setMaximumFractionDigits(1);

        final String percentString =
                latexPad(total == 0 ? "-" :
                        percent.format((double)val / (double)total), 5).replace("%", "\\%");
        final String numerator = latexPad(val, longestN);
        final String denominator = showDenominator == null ? latexPad(total, longestD) : showDenominator;

        String toShow;

        if (showOverride == null || showOverride.isEmpty()) {
            if (denominator.isEmpty()) {
                toShow = String.format("(%s)", numerator);
            } else {
                toShow = String.format("(%s/%s)", numerator, denominator);
            }
        } else {
            toShow = showOverride;
        }

        return String.format("%s & %s", percentString, toShow);
    }

    public static String justRatio(int val, int total, boolean showEquals) {
        final NumberFormat ratio = NumberFormat.getNumberInstance();
        ratio.setMinimumFractionDigits(1);
        ratio.setMaximumFractionDigits(1);

        if (showEquals) {
            return val + "/" + total + " = " + ratio.format((double)val / (double)total);
        } else {
            return ratio.format((double)val / (double)total);
        }
    }

    public static String ratio(int val, int total, int longestN, int longestD, int minLength, String showDenominator) {
        final NumberFormat ratio = NumberFormat.getNumberInstance();
        ratio.setMinimumFractionDigits(1);
        ratio.setMaximumFractionDigits(1);

        final String ratioString =
                latexPad(total == 0 ? "-" : ratio.format((double)val / (double)total), minLength);
        final String numerator = latexPad(val, longestN);
        final String denominator = showDenominator == null ? latexPad(total, longestD) : showDenominator;

        return String.format("%s & (%s/%s)", ratioString, numerator, denominator);
    }

    public static <T> int getLongestString(Stream<T> stream) {
        return stream.map(Object::toString).map(String::length).max(Integer::compareTo).orElse(0);
    }

    private final List<List<Cell>> cells = new ArrayList<>();

    private final Map<String, CellType> rowShow = new HashMap<>();
    private final Map<String, CellType> columnShow = new HashMap<>();

    private final List<String> columns;
    private final List<String> rows;

    private List<String> rowNames = new ArrayList<>();

    private boolean ignoreMissing = false;
    private String replaceMissing = "";

    public LatexTable(final List<String> columns, final List<String> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public Cell getCell(CellPos cellPos) {
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

    private Optional<Cell> makeCell(final Integer value, final Integer total, final CellType display) {
        if (value == null || total == null) {
            if (ignoreMissing) {
                return Optional.empty();
            } else if (replaceMissing != null && !replaceMissing.isEmpty()) {
                Cell cell = new Cell(0, 0, display);
                cell.showOverride = replaceMissing;
                return Optional.of(cell);
            }
        } else {
            return Optional.of(new Cell(value, total, display));
        }

        return Optional.empty();
    }

    public LatexTable addRow(final Map<String, Integer> m, final Map<String, Integer> totals, CellType display) {
        final List<Cell> newRow = new ArrayList<>();

        for (final String column : columns) {
            makeCell(m.get(column), totals.getOrDefault(column, 0), display)
                    .ifPresent(newRow::add);
        }

        cells.add(newRow);

        return this;
    }

    public LatexTable addRow(final Map<String, Integer> m,
                             final CellType display) {
        final List<Cell> newRow = new ArrayList<>();
        setRowDisplay(rows.get(cells.size()), display);

        int total = Util.total(m);

        for (final String column : columns) {
            makeCell(m.get(column), total, display)
                    .ifPresent(newRow::add);
        }

        cells.add(newRow);

        return this;
    }

    private static BiFunction<String, Integer, Integer> incrementBy(final int amount) {
        return (ignored, count) -> count == null ? amount : count + amount;
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
        for (final List<Cell> row : cells) {
            int total = row.stream().mapToInt(c -> c.value).sum();
            row.add(new Cell(total, 0, display));
        }

        columns.add(colName);

        return this;
    }

    public LatexTable addColumn(final String colName,
                                final Map<String, Integer> m,
                                final CellType display) {
        columns.add(colName);
//        setColumnDisplay(colName, display);

        int total = Util.total(m);

        for (int i = 0; i < rows.size(); i++) {
            if (cells.size() <= i) {
                cells.add(new ArrayList<>());
            }

            int finalI = i;
            makeCell(m.get(rows.get(i)), total, display)
                    .ifPresent(cell -> cells.get(finalI).add(cell));
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
                    .ifPresent(cell -> cells.get(finalI).add(cell));
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
        String result = "";

        for (int y = 0; y < cells.size(); y++) {
            List<Cell> row = cells.get(y);
            final List<String> cellStrs = new ArrayList<>();

            if (showRowNames) {
                if (rowNames.isEmpty()) {
                    cellStrs.add(rows.get(y));
                } else {
                    cellStrs.add(rowNames.get(y));
                }
            }

            for (int i = 0; i < row.size(); i++) {
                Cell cell = row.get(i);

                int longestN = 0;
                int longestD = 0;
                int minLength = 0;
                int longestVal = 0;
                for (final List<Cell> r : cells) {
                    longestN = Math.max(longestN, Integer.toString(r.get(i).value).length());
                    if (r.get(i).cellType != CellType.VALUE && r.get(i).cellType != CellType.VALUE_SINGLE_COL) {
                        longestD = Math.max(longestD, Integer.toString(r.get(i).total).length());
                    }
                    minLength = Math.max(minLength, r.get(i).justRatio().length());
                    longestVal = Math.max(longestVal, Integer.toString(r.get(i).value).length());
                }

                if (cell.cellType != CellType.DEFAULT) {
                    cellStrs.add(cell.showAs(longestN, longestD, minLength, longestVal, cell.cellType));
                } else {
                    final CellType rowType = rows.size() > y ? rowShow.getOrDefault(rows.get(y), CellType.DEFAULT) : CellType.DEFAULT;
                    final CellType colType = columns.size() > i ? columnShow.getOrDefault(columns.get(i), CellType.DEFAULT) : CellType.DEFAULT;

                    // Col type overrides row type unless it is default
                    if (colType == CellType.DEFAULT) {
                        cellStrs.add(cell.showAs(longestN, longestD, minLength, longestVal, rowType));
                    } else {
                        cellStrs.add(cell.showAs(longestN, longestD, minLength, longestVal, colType));
                    }
                }
            }

            result += String.join(" & ", cellStrs) + " \\\\ \\hline" + System.lineSeparator();
        }

        return result;
    }
}
