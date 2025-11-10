package net.ufrog.easy.authorizes;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 *
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class JWTAuthorize extends Authorize {

    /** 密钥 */
    private final String secret;

    /**
     * 构造函数
     *
     * @param isProduction 是否生产
     * @param ignoreURIs 忽略地址
     * @param filters 认证过滤器
     * @param secret 密钥
     */
    public JWTAuthorize(boolean isProduction, String[] ignoreURIs, AuthorizeFilter[] filters, String secret) {
        super(isProduction, ignoreURIs, filters);
        this.secret = secret;
    }

    @Override
    public String getToken(long id, long expire) {
        Date date = new Date(System.currentTimeMillis() + expire * 1000);
        return JWT.create().withAudience(String.valueOf(id)).withExpiresAt(date).sign(Algorithm.HMAC256(secret));
    }

    @Override
    public long checkToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return Long.parseLong(decodedJWT.getAudience().get(0));
        } catch (TokenExpiredException e) {
            log.warn(e.getMessage());
            return -1;
        } catch (Throwable e) {
            log.error(e.getMessage());
            return -2;
        }
    }
}
