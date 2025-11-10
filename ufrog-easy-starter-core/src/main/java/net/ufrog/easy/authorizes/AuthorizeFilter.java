package net.ufrog.easy.authorizes;

/**
 * 认证过滤器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public interface AuthorizeFilter {

    /**
     * 检查
     *
     * @param token 凭证
     * @param id 编号
     * @return 检查结果
     */
    boolean check(String token, long id);
}
