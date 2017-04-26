package cn.featherfly.spring.cache.redis;


import org.springframework.beans.BeansException;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * RedisCacheScaner
 * </p>
 * 
 * @author zhongj
 */
public class RedisCacheScaner implements ApplicationContextAware {

    private Collection<Cache> caches = new ArrayList<>();

    public RedisCacheScaner() {
    }

    public Collection<Cache> getCaches() {
        return caches;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, RedisCache> cacheMap = applicationContext.getBeansOfType(RedisCache.class);
        caches.addAll(cacheMap.values());
    }
}
