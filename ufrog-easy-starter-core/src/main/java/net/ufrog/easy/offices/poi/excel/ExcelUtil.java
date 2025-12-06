package net.ufrog.easy.offices.poi.excel;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.utils.ArrayUtil;
import net.ufrog.easy.utils.DateTimeUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * 表格工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-18
 * @since 3.5.3
 */
@Slf4j
public class ExcelUtil {

    /** 构造函数<br>不允许外部构造 */
    private ExcelUtil() {}

    /**
     * 读取表格
     *
     * @param inputStream 输入流
     * @param sheets 需要读取的表格片，若为空则读取文件内所有片
     * @return 表格工作簿
     */
    public static ExcelWorkbook read(final InputStream inputStream, final ExcelSheet... sheets) {
        log.debug("Begin reading excel file...");
        DateTimeUtil.Timer timer = new DateTimeUtil.Timer();
        ExcelWorkbook excelWorkbook;



        return null;
    }

    /**
     * 读取新版表格
     *
     * @param inputStream 输入流
     * @param excelSheets 需要读取的表格片，若为空则读取文件内所有片
     * @return 表格工作簿
     */
    private static ExcelWorkbook readExcel07(final InputStream inputStream, final ExcelSheet[] excelSheets) {
        try {
            OPCPackage opcPackage = OPCPackage.open(inputStream);
            XSSFReader xssfReader = new XSSFReader(opcPackage);
            ExcelWorkbook excelWorkbook = new ExcelWorkbook(xssfReader);
            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) xssfReader.getSheetsData();

            for (int i = 0; sheetIterator.hasNext(); i++) {
                try (InputStream sheet =  sheetIterator.next()) {
                    ExcelSheet excelSheet = matchExcel07Sheet(excelSheets, sheetIterator, i);
                    if (excelSheet != null) {
                        try {
                            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
                            excelWorkbook.addSheet(excelSheet);
                            parseExcel07Sheet(excelSheet, sheet, xmlReader, (SharedStringsTable) xssfReader.getSharedStringsTable(), xssfReader.getStylesTable());
                        } catch (SAXException | ParserConfigurationException e) {
                            throw new ExcelException(excelSheet, -1, -1, null, e);
                        }
                    }
                }
            }
            return null;
        } catch (IOException | OpenXML4JException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 匹配新版表格片
     *
     * @param excelSheets 需要读取的表格片，若为空则读取文件内所有片
     * @param sheetIterator 片迭代
     * @param index 片索引
     * @return 匹配到的片，若未匹配成功返回空
     */
    private static ExcelSheet matchExcel07Sheet(final ExcelSheet[] excelSheets, final XSSFReader.SheetIterator sheetIterator, final int index) {
        if (ArrayUtil.isEmpty(excelSheets)) {
            return ExcelSheet.builder().index(index).name(sheetIterator.getSheetName()).build();
        } else {
            for (ExcelSheet excelSheet : excelSheets) {
                if (excelSheet.check(index, sheetIterator.getSheetName())) {
                    return excelSheet;
                }
            }
            return null;
        }
    }

    /**
     * 解析新版表格片
     *
     * @param excelSheet 表格片
     * @param sheet 片输入流
     * @param reader 阅读器
     * @param sharedStringsTable 字符串共享表
     * @param stylesTable 样式表
     */
    private static void parseExcel07Sheet(final ExcelSheet excelSheet, final InputStream sheet, final XMLReader reader,
                                          final SharedStringsTable sharedStringsTable, final StylesTable stylesTable) {
        try {
            log.debug("Begin parsing excel sheet {} - {}.", excelSheet.getIndex(), excelSheet.getName());
            InputSource inputSource = new InputSource(sheet);
            if (reader.getContentHandler() == null) {
                reader.setContentHandler(new ExcelContent07Handler(excelSheet, sharedStringsTable, stylesTable));
            }
            reader.parse(inputSource);
            log.debug("Completed parsing excel sheet {} - {}.", excelSheet.getIndex(), excelSheet.getName());
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
