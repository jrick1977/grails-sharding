import com.jeffrick.grails.plugin.sharding.Index
import com.jeffrick.grails.plugins.sharding.Shard
import com.jeffrick.grails.plugins.services.ShardService

datasources = {
  datasource(name: Index.get().name) {bean ->
      domainClasses([Index.getIndexDomainClass(), Shard])
      services([ShardService])
      readOnly(false)
      driverClassName(Index.get().driverClass)
      url(Index.get().jdbcUrl)
      username(Index.get().user)
      password(Index.get().password)
      dbCreate('update')
      loggingSql(false)
      logSql(false)
      dialect(Index.get().dialect)
      pooled(true)

      hibernate {
        cache {
          provider_class('com.jeffrick.hibernate.cache.ShardCacheProvider')
          use_second_level_cache(true)
          use_query_cache(true)
        }
      }
    }
}
