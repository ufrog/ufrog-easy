package net.ufrog.easy.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-06-05
 * @since 3.5.3
 */
@Slf4j
public class FileUtilTests {

    @Test
    public void testGetExtension() {
        String filename = "test.txt";
        String extension = FileUtil.getExtension(filename);

        log.info("Extension: {}", extension);
        Assert.assertEquals("txt", extension);
    }
}
