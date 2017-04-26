package cn.featherfly.spring.cache.redis;

import java.util.concurrent.Callable;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.serializer.RedisSerializer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <p>
 * RedisCache
 * </p>
 * 
 * @author zhongj
 */
public class RedisCache implements Cache {

    private String name;

    private final RedisSerializer<Object> keySerializer;

    private final RedisSerializer<Object> valueSerializer;

    private final JedisPool jedisPool;

    private Integer expireSeconds = null;

    private boolean updateExprireOnGet;

    /**
     * @param name cacheName
     * @param keySerializer keySerializer
     * @param valueSerializer valueSerializer
     * @param jedisPool jedisPool
     */
    public RedisCache(String name, RedisSerializer<Object> keySerializer, RedisSerializer<Object> valueSerializer
            , JedisPool jedisPool) {
        this.name = name;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.jedisPool = jedisPool;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(Object key) {
        if (key != null) key = key.toString();
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(keySerializer.serialize(key));
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ValueWrapper get(Object key) {
        byte[] cache = getObjectBytes(key);
        if (cache == null) {
            return null;
        } else if (cache.length == 0) {
            // 表示有key存在，但是没有对象，即指定key没有内容，防止key没有对应的内容而一直查询数据库
            return new SimpleValueWrapper(null);
        } else {
            return new SimpleValueWrapper(deserialize(cache));
        }
//        Object cache = getObject(key);
//        return cache == null ? null : new SimpleValueWrapper(cache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T get(Object key, Class<T> type) {
        return getObject(key);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        byte[] cache = getObjectBytes(key);
        if (cache != null) {
            return (T) deserialize(cache);
        } else {
            return loadValue(key, valueLoader);
        }
    }

    private <T> T loadValue(Object key, Callable<T> valueLoader) {
        T value;
        try {
            value = valueLoader.call();
        }
        catch (Exception ex) {
            throw new ValueRetrievalException(key, valueLoader, ex);
        }
        put(key, value);
        return value;
    }

    private byte[] getObjectBytes(Object key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return getObjectBytes(jedis, key);
        }
    }

    private byte[] getObjectBytes(Jedis jedis, Object key) {
        if (key != null) key = key.toString();
        else return null;
        byte[] k = keySerializer.serialize(key);
        // 判断是否在get时更新缓存过期时间
        if (updateExprireOnGet && isUseExpire()) {
            jedis.expire(k, expireSeconds);
        }
        return jedis.get(k);
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(byte[] object) {
        return (T) valueSerializer.deserialize(object);
    }

    @SuppressWarnings("unchecked")
    private <T> T getObject(Object key) {
        if (key != null) key = key.toString();
        else return null;
        try (Jedis jedis = jedisPool.getResource()) {
            return (T) deserialize(getObjectBytes(jedis, key));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(Object key, Object value) {
        if (key != null) key = key.toString();
        try (Jedis jedis = jedisPool.getResource()) {
            // 判断是否设置超时
            if (isUseExpire()) {
                jedis.setex(keySerializer.serialize(key), expireSeconds, valueSerializer.serialize(value));
            } else {
                jedis.set(keySerializer.serialize(key), valueSerializer.serialize(value));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (key != null) key = key.toString();
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] k = keySerializer.serialize(key);
            Long result = jedis.setnx(keySerializer.serialize(key), valueSerializer.serialize(value));
            if (result == 0) {
                // 存在，放入失败，返回已存在的值
                return get(key);
            } else {
                //不存在，放入成功，设置超时时间
                if (expireSeconds != null && expireSeconds > 0) {
                    jedis.expire(k, expireSeconds);
                }
                return null;
            }
        }
    }

    private boolean isUseExpire() {
        return expireSeconds != null && expireSeconds > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getNativeCache() {
        return jedisPool;
    }

    public Integer getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(Integer expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public boolean isUpdateExprireOnGet() {
        return updateExprireOnGet;
    }

    public void setUpdateExprireOnGet(boolean updateExprireOnGet) {
        this.updateExprireOnGet = updateExprireOnGet;
    }
}
