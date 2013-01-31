package com.jeffrick.grails.plugin.sharding

import java.sql.Connection
import java.sql.SQLException

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource

/**
 * Reads the current shard we should point to and maintains that connection.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 */
class ShardingDS extends AbstractRoutingDataSource implements ApplicationContextAware {
  ApplicationContext applicationContext
  Integer shardType

  Connection getConnection() throws SQLException {
    Connection con = super.getConnection()
    if (con.getAutoCommit() != CurrentShard.getAutoCommit()) {
      con.setAutoCommit CurrentShard.getAutoCommit()
    }

    return con
  }

  protected Object determineCurrentLookupKey() {
    return CurrentShard.getIndex()
  }
}
