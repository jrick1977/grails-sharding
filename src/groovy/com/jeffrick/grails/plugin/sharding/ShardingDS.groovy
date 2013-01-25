package com.jeffrick.grails.plugin.sharding


import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import java.sql.SQLException
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import java.sql.Connection

/**
 * This is an implementation of AbstractRoutingDataSource that reads the
 * current shard we should point to and maintains that connection.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 */
class ShardingDS extends AbstractRoutingDataSource implements ApplicationContextAware {
  def applicationContext
  def Integer shardType

  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext
  }

  public Connection getConnection() throws SQLException {
    Connection con = super.getConnection();
    if (con.getAutoCommit() != CurrentShard.getAutoCommit()) {
      con.setAutoCommit CurrentShard.getAutoCommit();
    }

    return con;
  }

  protected Object determineCurrentLookupKey() {
    def env = CurrentShard.getIndex()
    return env
  }
}
