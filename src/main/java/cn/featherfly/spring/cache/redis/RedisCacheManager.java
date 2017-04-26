package cn.featherfly.spring.cache.redis;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;

import cn.featherfly.common.lang.LangUtils;

/**
 * <p>
 * RedisCacheManager
 * </p>
 * 
 * @author zhongj
 */
public class RedisCacheManager extends AbstractTransactionSupportingCacheManager {

    public RedisCacheManager(Collection<Cache> caches) {
        super();
        initCache(caches);
    }
    private void initCache(Collection<Cache> caches) {
        if (LangUtils.isNotEmpty(caches)) {
            caches.forEach(cache -> {
                if (this.caches.containsKey(cache.getName())) {
                    RedisCacheException.duplicateCacheNameException(cache.getName());
                }
                this.caches.put(cache.getName(), cache);
            });
        }
    }
    
    // fast lookup by name map
    private Map<String, Cache> caches = new ConcurrentHashMap<>();

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return this.caches.values();
    }
}
