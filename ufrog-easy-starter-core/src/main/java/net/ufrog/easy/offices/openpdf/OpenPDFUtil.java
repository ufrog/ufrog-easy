package net.ufrog.easy.offices.openpdf;

import com.lowagie.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationContext;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.offices.FontResolver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * PDF 工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-16
 * @since 3.5.3
 */
@Slf4j
public class OpenPDFUtil {

    /** 字体处理接口 */
    private static FontResolver fontResolver;

    /** 构造函数<br>不允许外部构造 */
    private OpenPDFUtil() {}

    /**
     * 创建文件
     *
     * @param html 超文本标签语言
     * @param outputStream 输出流
     */
    public static void createPDF(String html, OutputStream outputStream) {
        ITextRenderer renderer = new ITextRenderer();
        ITextFontResolver fontResolver = (ITextFontResolver) renderer.getSharedContext().getFontResolver();
        List<FontResolver.Font> fonts = getFonts();
        Document document = Jsoup.parse(html);

        // Add font & set default font-family
        if (fonts != null && !fonts.isEmpty()) {
            FontResolver.Font font = fonts.get(0);
            try {
                fontResolver.addFont(font.getPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                document.body().attr("style", "font-family:" + font.getName());
            } catch (IOException e) {
                throw CommonException.newInstance(e);
            }
        }

        // Set document setting & render
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        renderer.setDocumentFromString(document.outerHtml());
        renderer.layout();
        renderer.createPDF(outputStream);
    }

    /**
     * 创建文件
     *
     * @param html 超文本标签语言
     * @return 字节数组
     */
    public static byte[] createPDF(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            createPDF(html, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 读取字体列表
     *
     * @return 字体列表
     */
    private static List<FontResolver.Font> getFonts() {
        if (fontResolver == null) fontResolver = ApplicationContext.getBean(FontResolver.class);
        return fontResolver.getFonts();
    }
}
