package net.ufrog.easy.offices.poi.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 新版表格内容处理器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-23
 * @since 3.5.3
 */
@Slf4j
public class ExcelContent07Handler extends DefaultHandler {

    /** 表格片 */
    private final ExcelSheet excelSheet;

    /** 字符串共享表 */
    private final SharedStringsTable sharedStringsTable;

    /** 样式表 */
    private final StylesTable stylesTable;

    /**
     * 构造函数
     *
     * @param excelSheet 表格片
     * @param sharedStringsTable 字符串共享表
     * @param stylesTable 样式表
     */
    public ExcelContent07Handler(ExcelSheet excelSheet, SharedStringsTable sharedStringsTable, StylesTable stylesTable) {
        this.excelSheet = excelSheet;
        this.sharedStringsTable = sharedStringsTable;
        this.stylesTable = stylesTable;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        log.trace("Start parsing element {} - {}.", qName, localName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {}

    @Override
    public void characters(char[] ch, int start, int length) {}
}
