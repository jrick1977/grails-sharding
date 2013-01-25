package com.jeffrick.hibernate.cache;

import com.jeffrick.grails.plugin.sharding.CurrentShard;
import com.jeffrick.grails.plugin.sharding.Index;
import com.jeffrick.grails.plugin.sharding.ShardConfig;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;

import java.util.Map;

/**
 * This wrapper provides a prefix for the cache based on the current shard db
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 * Date: Feb 14, 2010
 * Time: 11:30:28 AM
 */
public class ShardCacheWrapper implements Cache {
    protected Cache baseCache;

    public ShardCacheWrapper(Cache cache) {
        baseCache = cache;
    }

    protected String getPrefix() {
        if(getRegionName().equals(Index.getIndexDomainClass())) {
            return("INDEX");
        } else {
            ShardConfig current = CurrentShard.get();
            return(current.getName());
        }
    }

    protected Object getKey(Object baseKey) {
        return (getPrefix() + baseKey);
    }

    public Object read(Object o) throws CacheException {
        return (baseCache.read(getKey(o)));
    }

    public Object get(Object o) throws CacheException {
        return (baseCache.get(getKey(o)));
    }

    public void put(Object o, Object o1) throws CacheException {
        baseCache.put(getKey(o), o1);
    }

    public void update(Object o, Object o1) throws CacheException {
        baseCache.update(getKey(o), o1);
    }

    public void remove(Object o) throws CacheException {
        baseCache.remove(getKey(o));
    }

    public void clear() throws CacheException {
        baseCache.clear();
    }

    public void destroy() throws CacheException {
        baseCache.destroy();
    }

    public void lock(Object o) throws CacheException {
        baseCache.lock(getKey(o));
    }

    public void unlock(Object o) throws CacheException {
        baseCache.unlock(getKey(o));
    }

    public long nextTimestamp() {
        return baseCache.nextTimestamp();
    }

    public int getTimeout() {
        return baseCache.getTimeout();
    }

    public String getRegionName() {
        return baseCache.getRegionName();
    }

    public long getSizeInMemory() {
        return baseCache.getSizeInMemory();
    }

    public long getElementCountInMemory() {
        return baseCache.getElementCountInMemory();
    }

    public long getElementCountOnDisk() {
        return baseCache.getElementCountOnDisk();
    }

    public Map toMap() {
        return baseCache.toMap();
    }
}
