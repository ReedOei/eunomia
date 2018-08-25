package com.reedoei.eunomia.latex;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.NumberFormat;

// TODO: Make different kinds of cells instead of using celltype
// TODO: Add ability to bold/italicize/surround with arbitrary latex commands/apply function to display string
public class RationalCell {
    public int value;
    public int total;
    public CellType cellType;
    public boolean isHidden = false;
    public boolean isBold = false;
    public boolean isItalic = false;
    public @Nullable String showOverride = null;

    public RationalCell(int value, int total, CellType type) {
        this.value = value;
        this.total = total;
        this.cellType = type;
    }

    public RationalCell(int value, int total, CellType type, boolean isHidden) {
        this.value = value;
        this.total = total;
        this.cellType = type;
        this.isHidden = isHidden;
    }

    @NonNull
    public static String justPercent(int val, int total, boolean showEquals) {
        final NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMinimumFractionDigits(1);
        percent.setMaximumFractionDigits(1);

        if (showEquals) {
            return val + "/" + total + " = " + percent.format((double)val / (double)total);
        } else {
            if (total == 0) {
                return "-";
            } else {
                return percent.format((double)val / (double)total).replace("%", "\\%");
            }
        }
    }

    @NonNull
    public static String percent(int val, int total, int longestN, int longestD,
                                 final @Nullable String showDenominator, final @Nullable String showOverride) {
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

    @NonNull
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

    public static String ratio(int val, int total, int longestN, int longestD, int minLength,
                               final @Nullable String showDenominator) {
        final NumberFormat ratio = NumberFormat.getNumberInstance();
        ratio.setMinimumFractionDigits(1);
        ratio.setMaximumFractionDigits(1);

        final String ratioString =
                latexPad(total == 0 ? "-" : ratio.format((double)val / (double)total), minLength);
        final String numerator = latexPad(val, longestN);
        final String denominator = showDenominator == null ? latexPad(total, longestD) : showDenominator;

        return String.format("%s & (%s/%s)", ratioString, numerator, denominator);
    }

    public static <T> String latexPad(final @NonNull T value, int i) {
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

    public String justPercent() {
        return justPercent(value, total, false);
    }

    public String justRatio() {
        return justRatio(value, total, false);
    }

    @NonNull
    public String percent(int longestN, int longestD, @Nullable final String showDenominator) {
        return percent(value, total, longestN, longestD, showDenominator, showOverride);
    }

    @NonNull
    public String ratio(int longestN, int longestD, int minLength, final @Nullable String showDenominator) {
        return ratio(value, total, longestN, longestD, minLength, showDenominator);
    }

    @NonNull
    public String showAs(int longestN, int longestD, int minLength, int longestVal, CellType cellType) {
        if (isHidden) {
            if (cellType == CellType.VALUE_SINGLE_COL) {
                return "";
            } else {
                return "&";
            }
        }

        if (showOverride != null && !showOverride.isEmpty()) {
            return showOverride;
        }

        final String str = formatType(longestN, longestD, minLength, longestVal, cellType);

        if (isBold) {
            return String.format("\\textbf{%s}", str);
        } else if (isItalic) {
            return String.format("\\textif{%s}", str);
        } else {
            return str;
        }
    }

    private String formatType(int longestN, int longestD, int minLength, int longestVal, CellType cellType) {
        switch (cellType) {
            case PERCENT:
                return percent(longestN, longestD, null);
            case PERCENT_NO_DENOM:
                return percent(longestN, longestD, "");
            case RATIO:
                return ratio(longestN, longestD, minLength, null);
            case JUST_RATIO:
                return justRatio();
            case VALUE:
                return "& " + latexPad(value, longestVal);
            case JUST_PERCENT:
                return justPercent();
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
