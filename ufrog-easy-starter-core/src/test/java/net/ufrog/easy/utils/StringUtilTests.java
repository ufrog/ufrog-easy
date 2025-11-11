package net.ufrog.easy.utils;

import net.ufrog.easy.utils.beans.A;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-11
 * @since 3.5.3
 */
public class StringUtilTests {

    private final static String A   = "";
    private final static String B   = "hello world";
    private final static String C   = " ";
    private final static String DEF = "God bless me";

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(StringUtil.isEmpty(A));
        Assert.assertFalse(StringUtil.isEmpty(B));
        Assert.assertFalse(StringUtil.isEmpty(C));
    }

    @Test
    public void testGetOrDefault() {
        Assert.assertEquals(DEF, StringUtil.getOrDefault(A, DEF));
        Assert.assertEquals(B, StringUtil.getOrDefault(B, DEF));
        Assert.assertEquals(C, StringUtil.getOrDefault(C, DEF));
    }

    @Test
    public void testGetOrElse() {
        Assert.assertEquals(DEF, StringUtil.getOrElse(A, () -> DEF));
        Assert.assertEquals(B, StringUtil.getOrElse(B, () -> DEF));
        Assert.assertEquals(C, StringUtil.getOrElse(C, () -> DEF));
    }

    @Test
    public void testIfNotEmpty() {
        StringUtil.ifNotEmpty(B, v -> Assert.assertEquals("hello world", v));
        StringUtil.ifNotEmpty(A, v -> Assert.fail(v + " is not empty."));
    }

    @Test
    public void testRandom() {
        int len = 32;
        String str = StringUtil.random(len, StringUtil.Set.SYMBOL, StringUtil.Set.UPPERCASE, StringUtil.Set.LOWERCASE, StringUtil.Set.NUMERIC);
        Assert.assertFalse(StringUtil.isEmpty(str));
        Assert.assertEquals(len, str.length());
    }

    @Test
    public void testTrimAllFields() {
        A a = new A();
        a.setE(" hello world ");
        a.setA(1);
        a.setD(10000L);

        A aa = StringUtil.trimAllFields(a);
        Assert.assertEquals("hello world", aa.getE());
        Assert.assertEquals(1, aa.getA());
        Assert.assertEquals(10000L, aa.getD().longValue());
    }
}
