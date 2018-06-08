package com.reedoei.eunomia.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DateUtilTest {
    @Test
    public void makeDateYearMonthDay() throws Exception {
        final Date date = DateUtil.makeDate(2014, 9, 2);
    }

    @Test
    public void makeDateAll() throws Exception {
    }
}