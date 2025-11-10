package net.ufrog.easy.caches;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.utils.ObjectUtil;
import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;

import java.io.*;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Redis 缓存实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class RedisImpl implements Cache {

    /** 连接池 */
    private final JedisPool jedisPool;

    /**
     * 构造函数
     *
     * @param host 地址
     * @param port 端口
     * @param password 密码
     * @param database 数据库
     */
    public RedisImpl(final String host, final int port, final String password, final int database) {
        HostAndPort hostAndPort = new HostAndPort(host, port);
        JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().password(password).database(database).build();
        jedisPool = new JedisPool(hostAndPort, jedisClientConfig);
    }

    @Override
    public boolean safeAdd(String key, Object value, int timeToLive) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.exists(key.getBytes())) return false;
            jedis.set(key.getBytes(), serialize(value), SetParams.setParams().nx().ex(timeToLive));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void add(String key, Object value, int timeToLive) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.exists(key.getBytes())) return;
            jedis.set(key.getBytes(), serialize(value), SetParams.setParams().nx().ex(timeToLive));
        }
    }

    @Override
    public boolean safeSet(String key, Object value, int timeToLive) {
        try (Jedis jedis = jedisPool.getResource()) {
            SetParams setParams = SetParams.setParams().ex(timeToLive);
            ObjectUtil.ifTrueOrElse(jedis.exists(key.getBytes()), setParams::xx, setParams::nx);
            jedis.set(key.getBytes(), serialize(value), setParams);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void set(String key, Object value, int timeToLive) {
        try (Jedis jedis = jedisPool.getResource()) {
            SetParams setParams = SetParams.setParams().ex(timeToLive);
            ObjectUtil.ifTrueOrElse(jedis.exists(key.getBytes()), setParams::xx, setParams::nx);
            jedis.set(key.getBytes(), serialize(value), setParams);
        }
    }

    @Override
    public boolean safeReplace(String key, Object value, int timeToLive) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.exists(key.getBytes())) return false;
            jedis.set(key.getBytes(), serialize(value), SetParams.setParams().xx().ex(timeToLive));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void replace(String key, Object value, int timeToLive) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.exists(key.getBytes())) return;
            jedis.set(key.getBytes(), serialize(value), SetParams.setParams().xx().ex(timeToLive));
        }
    }

    @Override
    public boolean safeRemove(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.decr(key.getBytes());
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void remove(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key.getBytes());
        }
    }

    @Override
    public Optional<Object> get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.exists(key.getBytes())) return Optional.ofNullable(deserialize(jedis.get(key.getBytes())));
            return Optional.empty();
        }
    }

    @Override
    public long incrementAndGet(String key, int by, int timeToLive, Supplier<Long> supplier) {
        try (Jedis jedis = jedisPool.getResource()) {
            return checkAndSet(jedis, key, timeToLive, supplier) ? jedis.incrBy(key, by) : -1;
        }
    }

    @Override
    public long decrementAndGet(String key, int by, int timeToLive, Supplier<Long> supplier) {
        try (Jedis jedis = jedisPool.getResource()) {
            return checkAndSet(jedis, key, timeToLive, supplier) ? jedis.decrBy(key, by) : -1;
        }
    }

    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    /**
     * 序列化
     *
     * @param obj 对象
     * @return 序列化字节数组
     */
    private byte[] serialize(Object obj) {
        if (obj == null) return new byte[0];
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
            log.trace("Serialize class: {}.", obj.getClass().getName());
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化
     *
     * @param bytes 字节数组
     * @return 对象
     */
    private Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes); ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            Object object = objectInputStream.readObject();
            log.trace("Deserialize class: {}.", object.getClass().getName());
            return object;
        } catch (IOException | ClassNotFoundException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 检查并设定初始值
     *
     * @param jedis 链接
     * @param key 标识
     * @param timeToLive 有效时间
     * @param supplier 数据提供
     * @return 判断结果
     */
    private boolean checkAndSet(Jedis jedis, String key, int timeToLive, Supplier<Long> supplier) {
        String value = jedis.exists(key) ? jedis.get(key) : null;
        if (value == null && supplier == null) {
            log.warn("Key {} not exists.", key);
            return false;
        } else if (value == null) {
            value = String.valueOf(supplier.get());
            jedis.set(key, value, SetParams.setParams().nx().ex(timeToLive));
            log.debug("Initialize {} value {}.", key, value);
        }
        return true;
    }
}
