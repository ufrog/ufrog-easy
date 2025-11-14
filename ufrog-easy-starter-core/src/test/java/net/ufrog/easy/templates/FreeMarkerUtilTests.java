package net.ufrog.easy.templates;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public class FreeMarkerUtilTests {

    @Test
    public void testRender() {
        String template = "Hello ${name}";
        Map<String, Object> map = new HashMap<>();

        map.put("name", "World");
        Assert.assertEquals("Hello World", FreeMarkerUtil.render("test", template, map));
        Assert.assertEquals("Hello World", FreeMarkerUtil.render("template.tpl", map).trim());
    }
}
