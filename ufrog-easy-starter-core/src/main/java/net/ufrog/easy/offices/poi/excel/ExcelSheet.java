package net.ufrog.easy.offices.poi.excel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表格片
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-16
 * @since 3.5.3
 */
@Slf4j
@Getter
@Setter
public class ExcelSheet implements Serializable {

    @Serial
    private static final long serialVersionUID = -3789923025872466599L;

    /** 索引 */
    private int index;

    /** 名称 */
    private String name;

    /** 表格工作簿 */
    private ExcelWorkbook workbook;
}
