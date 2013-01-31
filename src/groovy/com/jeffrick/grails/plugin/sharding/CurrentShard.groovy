package com.jeffrick.grails.plugin.sharding

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

import com.jeffrick.grails.plugin.sharding.annotation.Shard as ShardAnnotation
import com.jeffrick.grails.plugins.sharding.Shard

/**
 * Holds the current shard we are pointing, values are stored as thread locals.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 */
class CurrentShard {

    private static final ThreadLocal<Integer> _shardIndex = new ThreadLocal<Integer>()
    private static final ThreadLocal<Boolean> _autoCommit = new ThreadLocal<Boolean>()
    private static final ThreadLocal<String> _indexDataSource = new ThreadLocal<String>()
    private static final ThreadLocal<Map<String, ConfigObject>> _dataSourceLookup = new ThreadLocal<Map<String,ConfigObject>>()

    static ShardConfig get() {
        Integer currentId = getIndex()
        for (shard in Shards) {
            if (shard.id == currentId) {
                return shard
            }
        }
    }

    /**
     * Gets the index of the current shard
     */
    static Integer getIndex() {
        if (_shardIndex.get() == null) {
            _shardIndex.set(1)
        }
        return (_shardIndex.get())
    }

    /**
     * Gets whether the current shard is set to Auto Commit
     */
    static boolean getAutoCommit() {
        if (_autoCommit.get() == null) {
            return true
        } else {
            return (_autoCommit.get())
        }
    }

    /**
     * Sets whether the current shard is set to Auto Commit
     */
    static void setAutoCommit(boolean autoCommit) {
        _autoCommit.set autoCommit
    }

    /** *
     * Sets the current shard, requires a map that contains a index element that refers to the index
     * for the shard
     */
    static void setShard(ShardConfig shard) {
        _shardIndex.set(shard.id)
    }

    static void setDataSourceLookup(Map<String, ConfigObject> dataSourceLookup) {
        _dataSourceLookup.set(dataSourceLookup)
    }

    static String getIndexDatabaseURL() {
        String dataSourceName = getIndexDataSourceName()
        Map<String, ConfigObject> lookup = _dataSourceLookup.get()
        return(lookup.get(dataSourceName).getProperty("url"))
    }

    static String getIndexDataSourceName() {
        if (_indexDataSource.get() == null) {

            def grailsApplication = new Shard().domainClass.grailsApplication

            grailsApplication.domainClasses.each {
                DefaultGrailsDomainClass domainClass ->
                    if (domainClass.clazz.isAnnotationPresent(ShardAnnotation)) {
                        ShardAnnotation prop = domainClass.clazz.getAnnotation(ShardAnnotation)
                        _indexDataSource.set(prop.indexDataSourceName())
                        return (prop.indexDataSourceName())
                    }
            }

        }
        if (_indexDataSource.get() != null) {
            return (_indexDataSource.get())
        }

        throw new Exception("Error no domain class registered as a Shard lookup class!")
    }
}
