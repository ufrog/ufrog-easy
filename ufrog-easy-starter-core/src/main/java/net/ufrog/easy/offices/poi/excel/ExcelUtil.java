package net.ufrog.easy.offices.poi.excel;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.utils.DateTimeUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;

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
            ExcelWorkbook excelWorkbook = new ExcelWorkbook();

            return null;
        } catch (IOException | OpenXML4JException e) {
            throw CommonException.newInstance(e);
        }
    }
}
