package net.ufrog.easy.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-11
 * @since 3.5.3
 */
@Slf4j
public class DateTimeUtilTests {

    @Test
    public void testToEndOf() {
        Date date = DateTimeUtil.fromString("2024-01-23", "yyyy-MM-dd");
        String end = DateTimeUtil.toString(DateTimeUtil.toEndOf(date, DateTimeUtil.Type.DAY), "yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals("2024-01-23 23:59:59", end);

        end = DateTimeUtil.toString(DateTimeUtil.toEndOf(date, DateTimeUtil.Type.MONTH), "yyyy-MM-dd");
        Assert.assertEquals("2024-01-31", end);

        end = DateTimeUtil.toString(DateTimeUtil.toEndOf(date, DateTimeUtil.Type.QUARTER), "yyyy-MM-dd");
        Assert.assertEquals("2024-03-31", end);

        end = DateTimeUtil.toString(DateTimeUtil.toEndOf(date, DateTimeUtil.Type.YEAR), "yyyy-MM-dd");
        Assert.assertEquals("2024-12-31", end);
    }

    @Test
    public void testTimer() {
        DateTimeUtil.Timer timer = new DateTimeUtil.Timer();
        log.info("Spend milliseconds: {}", timer.getSpend());
        log.info("Spend string: {}", timer.getSpendString());
    }
}
