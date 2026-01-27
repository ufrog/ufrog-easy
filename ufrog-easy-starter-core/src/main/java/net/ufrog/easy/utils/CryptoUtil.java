package net.ufrog.easy.utils;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.exceptions.CryptoException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 编码工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-01-22
 * @since 3.5.3
 */
@Slf4j
public class CryptoUtil {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /** 构造函数<br>不允许外部构造 */
    private CryptoUtil() {}

    /**
     * 哈希
     *
     * @param bytes 字节数组
     * @param hashType 哈希类型
     * @return 哈希后字节数组
     */
    public static byte[] hash(byte[] bytes, HashType hashType) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(hashType.toString());
            return messageDigest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    /**
     * 哈希
     *
     * @param str 字符串
     * @param hashType 哈希类型
     * @return 哈希后字节数组
     */
    public static byte[] hash(String str, HashType hashType) {
        return hash(str.getBytes(StandardCharsets.UTF_8), hashType);
    }

    /**
     * 转换成十六进制
     *
     * @param bytes 字节数组
     * @param toLowerCase 是否转成小写
     * @return 十六进制字符串
     */
    public static String encodeHex(final byte[] bytes, final boolean toLowerCase) {
        char[] digits = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
        char[] out = new char[bytes.length << 1];

        for (int i = 0, j = 0; i < bytes.length; i++) {
            out[j++] = digits[(0xF0 & bytes[i]) >>> 4];
            out[j++] = digits[0x0F & bytes[i]];
        }
        return new String(out);
    }

    /**
     * 转换成十六进制<br>默认小写字符
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String encodeHex(final byte[] bytes) {
        return encodeHex(bytes, true);
    }

    /**
     * 十六进制转换成字节数组
     *
     * @param str 十六进制字符串
     * @return 字节数组
     */
    public static byte[] decodeHex(final String str) {
        char[] chars = str.toCharArray();
        if ((chars.length & 0x01) != 0) throw new CryptoException("Cannot decode hex string, cause odd number of characters.");

        final byte[] out = new byte[chars.length >> 1];
        for (int i = 0, j = 0; j < chars.length; i++) {
            int f = toDigit(chars[j], j) << 4;
            j++;
            f = f | toDigit(chars[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    /**
     * 哈希并转换成十六进制字符串
     *
     * @param str 字符串
     * @param hashType 哈希类型
     * @return 哈希后字符串
     */
    public static String hashAndHex(String str, HashType hashType) {
        return encodeHex(hash(str, hashType));
    }

    /**
     * <i>MD5</i> 哈希
     *
     * @param str 字符串
     * @return 哈希后字符串
     */
    public static String md5(String str) {
        return hashAndHex(str, HashType.MD5);
    }

    /**
     * 密钥哈希
     *
     * @param bytes 字节数组
     * @param key 密钥
     * @param hmacType 哈希类型
     * @return 哈希后字节数组
     */
    public static byte[] hmac(byte[] bytes, byte[] key, HmacType hmacType) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, hmacType.toString());
            Mac mac = Mac.getInstance(hmacType.toString());
            mac.init(secretKey);
            return mac.doFinal(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    /**
     * 密钥哈希
     *
     * @param str 字符串
     * @param key 密钥
     * @param hmacType 哈希类型
     * @return 哈希后字节数组
     */
    public static byte[] hmac(String str, String key, HmacType hmacType) {
        return hmac(str.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8), hmacType);
    }

    /**
     * 密钥哈希并编译
     *
     * @param str 字符串
     * @param key 密钥
     * @param hmacType 哈希类型
     * @return 哈希并编译后字符串
     */
    public static String hmacAndBase64(String str, String key, HmacType hmacType) {
        return Base64.getEncoder().encodeToString(hmac(str, key, hmacType));
    }

    /**
     * 加密
     *
     * @param str 字符串
     * @param key 密钥
     * @param encryptType 加密类型
     * @return 加密后字符串
     */
    public static String encrypt(String str, String key, EncryptType encryptType) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), encryptType.toString());
            Cipher cipher = Cipher.getInstance(encryptType.toString());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 解密
     *
     * @param str 字符串
     * @param key 密钥
     * @param encryptType 加密类型
     * @return 解密后字符串
     */
    public static String decrypt(String str, String key, EncryptType encryptType) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), encryptType.toString());
            Cipher cipher = Cipher.getInstance(encryptType.toString());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(str)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * <i>AES</i> 加密
     *
     * @param str 字符串
     * @param key 密钥
     * @return 加密后字符串
     */
    public static String encryptAES(String str, String key) {
        return encrypt(str, key, EncryptType.AES);
    }

    /**
     * <i>AES</i> 解密
     *
     * @param str 字符串
     * @param key 密钥
     * @return 解密后字符串
     */
    public static String decryptAES(String str, String key) {
        return decrypt(str, key, EncryptType.AES);
    }

    /**
     * <i>DES</i> 加密
     *
     * @param str 字符串
     * @param key 密钥
     * @return 加密后字符串
     */
    public static String encryptDES(String str, String key) {
        return encrypt(str, key, EncryptType.DES);
    }

    /**
     * <i>DES</i> 解密
     *
     * @param str 字符串
     * @param key 密钥
     * @return 解密后字符串
     */
    public static String decryptDES(String str, String key) {
        return decrypt(str, key, EncryptType.DES);
    }

    /**
     * 加密密码
     *
     * @param password 密码
     * @return 加密后密码
     */
    public static String encodePassword(String password) {
        byte[] salt = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return encodePassword(password, salt);
    }

    /**
     * 加密密码
     *
     * @param password 密码
     * @param salt 盐
     * @return 加密后密码
     */
    public static String encodePassword(String password, final byte[] salt) {
        byte[] digest = CryptoUtil.hash(ArrayUtil.concatenateByteArray(salt, password.getBytes(StandardCharsets.UTF_8)), HashType.SHA256);
        return encodeHex(ArrayUtil.concatenateByteArray(salt, digest));
    }

    /**
     * 验证密码
     *
     * @param raw 待验证密码
     * @param encoded 加密后密码
     * @return 验证结果
     */
    public static Boolean matchesPassword(String raw, String encoded) {
        byte[] digest = decodeHex(encoded);
        byte[] salt = ArrayUtil.subByteArray(digest, 0, 8);
        return StringUtil.equals(encoded, encodePassword(raw, salt));
    }

    /**
     * 字符转换成数字
     *
     * @param ch 字符
     * @param index 位置
     * @return 数字
     */
    private static int toDigit(final char ch, final int index) {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new CryptoException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    /**
     * 哈希类型
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 1.0.0, 2018-11-15
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public enum HashType {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA512("SHA-512");

        /** 算法 */
        private final String algorithm;

        /**
         * 构造函数
         *
         * @param algorithm 算法
         */
        HashType(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String toString() {
            return algorithm;
        }
    }

    /**
     * 密钥哈希类型
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 1.0.0, 2018-11-15
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public enum HmacType {
        HMAC_MD5("HmacMD5"),
        HMAC_SHA1("HmacSHA1"),
        HMAC_SHA256("HmacSHA256"),
        HMAC_SHA224("HmacSHA224"),
        HMAC_SHA512("HmacSHA512"),
        HMAC_SHA384("HmacSHA384"),
        HMAC_SHA3("HmacSHA3"),
        HMAC_RIPEMD160("HmacRIPEMD160");

        /** 算法 */
        private final String algorithm;

        /**
         * 构造函数
         *
         * @param algorithm 算法
         */
        HmacType(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String toString() {
            return algorithm;
        }
    }

    /**
     * 加密类型
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 1.0.0, 2018-11-15
     * @since 1.0.0
     */
    public enum EncryptType {
        AES("AES"),
        DES("DES");

        /** 算法 */
        private final String algorithm;

        /**
         * 构造函数
         *
         * @param algorithm 算法
         */
        EncryptType(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String toString() {
            return algorithm;
        }
    }
}
