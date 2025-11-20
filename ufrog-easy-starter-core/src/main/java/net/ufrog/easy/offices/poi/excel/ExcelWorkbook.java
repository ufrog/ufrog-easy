package net.ufrog.easy.offices.poi.excel;

import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.utils.StringUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorkbookDocument;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 表格工作簿
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-16
 * @since 3.5.3
 */
@Getter
public class ExcelWorkbook implements Serializable {

    @Serial
    private static final long serialVersionUID = -1387459863897727480L;

    /** 表格片列表 */
    private List<ExcelSheet> sheets;

    /** 是否以1904年作为起始 */
    @Setter
    private boolean isDate1904;

    /** 构造函数 */
    public ExcelWorkbook() {
        this.isDate1904 = false;
    }

    /**
     * 构造函数
     *
     * @param reader 阅读器
     */
    public ExcelWorkbook(XSSFReader reader) {
        this();
        try (InputStream inputStream = reader.getWorkbookData()) {
            CTWorkbookPr ctWorkbookPr = WorkbookDocument.Factory.parse(inputStream).getWorkbook().getWorkbookPr();
            if (ctWorkbookPr != null) {
                this.isDate1904 = ctWorkbookPr.getDate1904();
            }
        } catch (InvalidFormatException | IOException | XmlException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 添加表格片
     *
     * @param sheet 表格片
     */
    public void addSheet(ExcelSheet sheet) {
        if (this.sheets == null) {
            this.sheets = new ArrayList<>();
        }
        sheets.add(sheet);
        sheet.setWorkbook(this);
    }

    /**
     * 按索引读取表格片
     *
     * @param index 索引
     * @return 表格片，若匹配不到则返回空
     */
    public ExcelSheet getSheetAt(int index) {
        if (this.sheets != null) {
            return this.sheets.stream()
                    .filter(v -> v.getIndex() == index)
                    .findFirst()
                    .orElse(null);
        }
        throw new NullPointerException("Sheet list is null.");
    }

    /**
     * 按名称读取表格片
     *
     * @param name 名称
     * @return 表格片，若匹配不到则返回空
     */
    public ExcelSheet getSheet(String name) {
        if (this.sheets != null) {
            return this.sheets.stream()
                    .filter(v -> StringUtil.equals(v.getName(), name))
                    .findFirst()
                    .orElse(null);
        }
        throw new NullPointerException("Sheet list is null.");
    }
}
