package net.ufrog.easy.storage;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.utils.DateTimeUtil;
import net.ufrog.easy.utils.StreamUtil;
import net.ufrog.easy.utils.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Optional;

/**
 * 本地存储
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-04-14
 * @since 3.5.3
 */
@Slf4j
public class LocalStorage implements Storage {

    /** 本地路径 */
    private final String localPath;

    /**
     * 构造函数
     *
     * @param localPath 本地路径
     */
    public LocalStorage(String localPath) {
        this.localPath = localPath;
        if (localPath != null) log.info("Set local file storage path: {}.", localPath);
    }

    @Override
    public String getPrefix() {
        return "lf:";
    }

    @Override
    public byte[] get(String key) {
        return getFile(key).map(file -> {
            try (FileInputStream fis = new FileInputStream(file)) {
                return StreamUtil.toByteArray(fis);
            } catch (IOException e) {
                throw CommonException.newInstance(e);
            }
        }).orElse(new byte[0]);
    }

    @Override
    public String get(String key, Charset charset) {
        return new String(get(key), charset);
    }

    @Override
    public String put(byte[] bytes, String originalFilename, String filename, String parent, boolean isAutoParent) {
        String name = StringUtil.getOrDefault(filename, StringUtil.uuid()) + (StringUtil.isEmpty(originalFilename) ? "" : "." + originalFilename.substring(originalFilename.lastIndexOf(".") + 1));
        String dirname = StringUtil.isEmpty(parent) ? "" : (parent.startsWith("/") ? "" : "/") + (isAutoParent ? "/" + DateTimeUtil.toString(new Date(), "yyyyMMdd") : "");
        File dir = new File(localPath, dirname);

        if (dir.exists() || (!dir.exists() && dir.mkdirs())) {
            File file = new File(dir, name);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
                log.info("Put file to local storage: {}.", file.getAbsolutePath());
            } catch (IOException e) {
                throw CommonException.newInstance(e);
            }
        }
        return getPrefix() + dirname + "/" + name;
    }

    @Override
    public String put(byte[] bytes, String originalFilename, String parent, boolean isAutoParent) {
        return put(bytes, originalFilename, null, parent, isAutoParent);
    }

    @Override
    public String put(byte[] bytes) {
        return put(bytes, null, null, null, true);
    }

    @Override
    public boolean replace(String key, byte[] bytes) {
        return getFile(key).map(file -> {
            if (delete(key)) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(bytes);
                    log.info("Replace file to local storage: {}.", key);
                    return true;
                } catch (IOException e) {
                    throw CommonException.newInstance(e);
                }
            } else {
                return false;
            }
        }).orElse(false);
    }

    @Override
    public boolean delete(String key) {
        return getFile(key).map(file -> {
            if (file.delete()) {
                log.info("Delete file from local storage: {}.", key);
                return true;
            } else {
                log.warn("Cannot delete file from local storage: {}.", key);
                return false;
            }
        }).orElse(false);
    }

    /**
     * 读取文件
     *
     * @param key 键值
     * @return 文件
     */
    private Optional<File> getFile(String key) {
        File file = new File(key.replace(getPrefix(), localPath));
        if (file.exists()) {
            return Optional.of(file);
        } else {
            log.warn("Cannot find file by key: {}.", key);
            return Optional.empty();
        }
    }
}
