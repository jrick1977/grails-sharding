package com.jeffrick.hibernate.cache;

import java.util.Properties;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.EhCacheProvider;

/**
 * Simple wrapper of the EhCacheProvider that uses ShardCacheWrapper instead of the default cache.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 */
public class ShardCacheProvider extends EhCacheProvider {
    @Override
    public Cache buildCache(String region, Properties properties) throws CacheException {
        Cache baseCache = super.buildCache(region, properties);

        return new ShardCacheWrapper(baseCache);
    }
}
