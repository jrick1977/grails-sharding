package com.jeffrick.hibernate.cache;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;

import java.util.Properties;

/**
 * Simple wrapper of the EhCacheProvider that uses ShardCacheWrapper instead of the default cache.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 * Date: Feb 14, 2010
 * Time: 11:26:51 AM
 */
public class ShardCacheProvider extends org.hibernate.cache.EhCacheProvider {
    @Override
    public Cache buildCache(String region, Properties properties) throws CacheException {
        Cache baseCache = super.buildCache(region, properties);

        ShardCacheWrapper cache = new ShardCacheWrapper(baseCache);
        return(cache);
    }
}
