package net.ufrog.easy.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-11
 * @since 3.5.3
 */
public class ArrayUtilTests {

    private final static Object[] A = new Object[0];
    private final static String[] B = new String[] { "a", "b", "c" };
    private final static Object[] DEF = new Object[] { "def1", "def2" };

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(ArrayUtil.isEmpty(A));
        Assert.assertFalse(ArrayUtil.isEmpty(B));
    }

    @Test
    public void testGetOrDefault() {
        Assert.assertArrayEquals(DEF, ArrayUtil.getOrDefault(A, DEF));
        Assert.assertArrayEquals(B, ArrayUtil.getOrDefault(B, DEF));
    }

    @Test
    public void testMerge() {
        String[] strings = new String[] { "a", "b", "c" };
        String[] result = ArrayUtil.merge(String.class, strings);

        Assert.assertEquals(3, result.length);
        Assert.assertEquals("c", result[2]);
        Assert.assertArrayEquals(strings, result);

        result = ArrayUtil.merge(String.class, null, "1", "2", "3", "4");
        Assert.assertEquals(4, result.length);
        Assert.assertEquals("2", result[1]);

        result = ArrayUtil.merge(String.class, strings, "1", "2", "3", "4");
        Assert.assertEquals(7, result.length);
        Assert.assertEquals("b", result[1]);
        Assert.assertEquals("4", result[6]);
    }

    @Test
    public void testArraycopy() {
        Integer[] dest = new Integer[] {1, 2, 3, 0, 0};
        Integer[] src = new Integer[] {4, 5};
        System.arraycopy(src, 0, dest, 3, 2);

        Stream.of(dest).forEach(System.out::print);
        Assert.assertEquals(5, dest.length);
        Assert.assertEquals(4, dest[3].intValue());
        Assert.assertEquals(5, dest[4].intValue());
        Assert.assertEquals(3, dest[2].intValue());
    }

    @Test
    public void testToArrayList() {
        List<String> list1 = ArrayUtil.toArrayList("hello", "world");
        Assert.assertEquals(2, list1.size());
        Assert.assertEquals("hello", list1.get(0));
        Assert.assertEquals("world", list1.get(1));

        List<String> list2 = ArrayUtil.toArrayList(new String[] {"hello", "world"}, new String[] {"you", "are", "the", "champion"});
        Assert.assertEquals(6, list2.size());
        Assert.assertEquals("hello", list2.get(0));
        Assert.assertEquals("you", list2.get(2));
        Assert.assertEquals("champion", list2.get(5));
    }
}
