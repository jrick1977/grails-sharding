package com.jeffrick.grails.plugin.sharding

import org.codehaus.groovy.grails.commons.ApplicationHolder as AH

/**
 * This class is used to hold the connection info for the INDEX database
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 * Date: Jul 5, 2010
 * Time: 5:53:58 PM
 */
class Index {
  private static Index _index

  int id
  String name
  String jdbcUrl
  String user
  String password
  String driverClass
  Boolean autoCommit = true
  String domainClass
  String shardNameFieldName
  Integer initialPoolSize
  Integer minPoolSize
  Integer maxPoolSize
  Boolean testConnectionOnCheckin = false
  Boolean testConnectionOnCheckout = false
  Integer maxIdleTime
  Integer maxConnectionAge
  String automaticTestTable
  Object dialect

  static {
    _index = loadIndex();
  }

  static set(Index currentIndex) {
    _index=currentIndex
  }

  static String getIndexDomainClass() {
    Index currentIndex = (Index) _index.get()
    return (currentIndex.domainClass);
  }

  static Index get() {
    return (_index)
  }

  static private Index loadIndex() {
    try {
      def script = AH.application.classLoader.loadClass('Shards').newInstance()
      script.run()

      def builder = new IndexBuilder()
      def index = script.index
      index.delegate = builder
      index()

      return builder.currentIndex
    }
    catch (e) {
      println e.message
      return []
    }
  }


}
