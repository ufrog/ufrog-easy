package net.ufrog.easy.templates;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.utils.FileUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * FreeMarker 工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public class FreeMarkerUtil {

    /** 构造函数<br>不允许外部构造 */
    private FreeMarkerUtil() {}

    /**
     * 渲染
     *
     * @param filename 文件名
     * @param data 填充数据
     * @return 渲染后内容
     */
    public static String render(final String filename, Map<String, Object> data) {
        String source = FileUtil.readAsString(filename, false);
        return render(filename, source, data);
    }

    /**
     * 渲染
     *
     * @param name 模版名称
     * @param source 模版内容
     * @param data 填充数据
     * @return 渲染后内容
     */
    public static String render(String name, String source, Map<String, Object> data) {
        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            configuration.setTemplateLoader(new StringTemplateLoader());

            Template template = new Template(name, source, configuration);
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw CommonException.newInstance(e);
        }
    }
}
