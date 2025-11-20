package net.ufrog.easy.offices.poi.excel;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 表格行
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-16
 * @since 3.5.3
 */
public class ExcelRow implements Serializable {

    @Serial
    private static final long serialVersionUID = 1086896918628982333L;

    /** 行号 */
    private int num;

    /** 表格单元格列表 */
    private List<ExcelCell> cells;

    /** 是否标题行 */
    private boolean isTitle;

    /** 表格片 */
    private ExcelSheet sheet;
}
