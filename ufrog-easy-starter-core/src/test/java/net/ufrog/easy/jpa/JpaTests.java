package net.ufrog.easy.jpa;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
public class JpaTests {

    @Test
    public void testModel() {
        String[] auditFields = EasyModel.AUDITOR_FIELDS;
        System.out.println(String.join(",", auditFields));
        Assert.assertEquals(11, auditFields.length);
        Assert.assertEquals("id", auditFields[3]);
        Assert.assertEquals("updateTime", auditFields[7]);
    }
}
