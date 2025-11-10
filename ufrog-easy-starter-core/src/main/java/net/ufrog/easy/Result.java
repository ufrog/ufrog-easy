package net.ufrog.easy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 结果
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
public class Result<T extends Serializable> {

    /** 是否成功 */
    private boolean success = false;

    /** 消息 */
    private String message;

    /** 数据 */
    private T data;
}
