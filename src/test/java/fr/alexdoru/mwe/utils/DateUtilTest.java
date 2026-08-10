package fr.alexdoru.mwe.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateUtilTest {

    @Test
    public void formatTimeTest() {
        assertEquals("0s", DateUtil.formatTime(0));
        assertEquals("10s", DateUtil.formatTime(10));
        assertEquals("59s", DateUtil.formatTime(59));
        assertEquals("1m", DateUtil.formatTime(60));
        assertEquals("1m 1s", DateUtil.formatTime(61));
        assertEquals("1m 29s", DateUtil.formatTime(60 + 29));
        assertEquals("2m", DateUtil.formatTime(120));
        assertEquals("2m 45s", DateUtil.formatTime(120 + 45));
    }

}
