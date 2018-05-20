package com.reedoei.eunomia.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date makeDate(final int year, final int month, final int day) {
        return makeDate(year, month, day, 0, 0, 0);
    }

    public static Date makeDate(final int year, final int month, final int day,
                                final int hour, final int minute, final int seconds) {
        final Calendar calendar = Calendar.getInstance();

        // Month starts at 0, not 1.
        calendar.set(year, month - 1, day, hour, minute, seconds);

        return calendar.getTime();
    }
}
