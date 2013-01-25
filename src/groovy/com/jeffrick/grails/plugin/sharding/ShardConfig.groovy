package com.jeffrick.grails.plugin.sharding

/**
 * Created by IntelliJ IDEA.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 * Date: Jun 16, 2010
 * Time: 5:12:30 PM
 */
class ShardConfig {
  int id
  String name
  String jdbcUrl
  String user
  String password
  String driverClass
  Integer capacity
  
  Integer initialPoolSize
  Integer minPoolSize
  Integer maxPoolSize
  Boolean testConnectionOnCheckin = false
  Boolean testConnectionOnCheckout = false
  Integer maxIdleTime
  Integer maxConnectionAge
  String automaticTestTable
    
  Boolean autoCommit = true
}
