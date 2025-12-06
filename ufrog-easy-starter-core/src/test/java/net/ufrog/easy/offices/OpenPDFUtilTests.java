package net.ufrog.easy.offices;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.offices.openpdf.OpenPDFUtil;
import net.ufrog.easy.utils.DateTimeUtil;
import net.ufrog.easy.utils.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-23
 * @since 3.5.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigBean.class})
@SpringBootTest
@Slf4j
public class OpenPDFUtilTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void before() {
        net.ufrog.easy.ApplicationContext.init(applicationContext);
    }

    @Test
    public void testCreatePDF() {
        String html = FileUtil.readAsString("resume.html", false);
        try (OutputStream out = new FileOutputStream("test_gen/pdf/resume_" + DateTimeUtil.toString(new Date(), "yyyyMMddHHmmss") + ".pdf")) {
            OpenPDFUtil.createPDF(html, out);
        } catch (Exception e) {
            throw CommonException.newInstance(e);
        }
    }
}
