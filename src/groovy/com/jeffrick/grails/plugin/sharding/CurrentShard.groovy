package com.jeffrick.grails.plugin.sharding

import com.jeffrick.grails.plugin.sharding.annotation.Shard
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

/**
 * This class holds the current shard we are pointing, values are stored as thread locals
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 */
class CurrentShard {

    private static final ThreadLocal _shardIndex
    private static final ThreadLocal _autoCommit
    private static final ThreadLocal _indexDataSource
    private static HashMap<String, ConfigObject> _dataSourceLookup

    static {
        _shardIndex = new ThreadLocal();
        _autoCommit = new ThreadLocal();
        _indexDataSource = new ThreadLocal();
        _dataSourceLookup = new HashMap<String,ConfigObject>()
    }

    static ShardConfig get() {
        Integer currentId = getIndex()
        ShardConfig current = null;
        Shards.getShards().each {
            if (it.id == currentId) {
                current = it
            }
        }

        return (current);
    }

    /** *
     * Gets the index of the current shard
     */
    static Integer getIndex() {
        if (_shardIndex.get() == null) {
            _shardIndex.set(1);
        }
        return ((long) _shardIndex.get())
    }

    /** *
     * Gets whether the current shard is set to Auto Commit
     */
    static boolean getAutoCommit() {
        if (_autoCommit.get() == null) {
            return true
        } else {
            return ((boolean) _autoCommit.get())
        }
    }

    /** *
     * Sets whether the current shard is set to Auto Commit
     */
    static void setAutoCommit(boolean autoCommit) {
        _autoCommit.set autoCommit;
    }

    /** *
     * Sets the current shard, requires a map that contains a index element that refers to the index
     * for the shard
     */
    static void setShard(ShardConfig shard) {
        _shardIndex.set(shard.id)
    }

    static void setDataSourceLookup(HashMap<String, ConfigObject> dataSourceLookup) {
        _dataSourceLookup = dataSourceLookup
    }

    static String getIndexDatabaseURL() {
        String dataSourceName = getIndexDataSourceName()
        return(_dataSourceLookup.get(dataSourceName).getProperty("url"))
    }

    static public String getIndexDataSourceName() {
        if (_indexDataSource.get() == null) {

            def grailsApplication = new com.jeffrick.grails.plugins.sharding.Shard().domainClass.grailsApplication

            grailsApplication.domainClasses.each {
                DefaultGrailsDomainClass domainClass ->
                    if (domainClass.clazz.isAnnotationPresent(Shard)) {
                        def prop = domainClass.clazz.getAnnotation(Shard)
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
