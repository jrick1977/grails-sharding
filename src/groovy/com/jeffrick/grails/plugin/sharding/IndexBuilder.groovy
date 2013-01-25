package com.jeffrick.grails.plugin.sharding

/**
 * Reads the index section of the configuration file.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 * Date: Jun 16, 2010
 * Time: 4:58:33 PM
 */

class IndexBuilder extends BuilderSupport {
  public Index currentIndex

  /**
   * {@inheritDoc}
   * @see groovy.util.BuilderSupport#createNode(java.lang.Object)
   */
  @Override
  protected Object createNode(Object name) {
    return(null)
  }

  /**
   * {@inheritDoc}
   * @see groovy.util.BuilderSupport#createNode(java.lang.Object, java.lang.Object)
   */
  @Override
  protected Object createNode(Object name, Object value) {
    if(currentIndex == null) {
      currentIndex = new Index()
    }

    switch(name) {
      case "domainClass":
      case "shardNameFieldName":
      case "name":
      case "user":
      case "password":
      case "driverClass":
      case "jdbcUrl":
      case "dialect":
      case "initialPoolSize":
      case "minPoolSize":
      case "maxPoolSize":
      case "testConnectionOnCheckin":
      case "testConnectionOnCheckout":
      case "maxIdleTime":
      case "maxConnectionAge":
      case "automaticTestTable":      
        currentIndex."$name" = value
        return name
    }

    throw new IllegalArgumentException("Cannot create node with name '$name' and value '$value'")
  }

  /**
   * {@inheritDoc}
   * @see groovy.util.BuilderSupport#createNode(java.lang.Object, java.util.Map)
   */
  @Override
  protected Object createNode(Object name, Map attributes) {
    throw new IllegalArgumentException("Cannot create node with name '$name'")
  }

  /**
   * {@inheritDoc}
   * @see groovy.util.BuilderSupport#createNode(java.lang.Object, java.util.Map, java.lang.Object)
   */
  @Override
  protected Object createNode(Object name, Map attributes, Object value) {
    throw new UnsupportedOperationException()
  }

  /**
   * {@inheritDoc}
   * @see groovy.util.BuilderSupport#setParent(java.lang.Object, java.lang.Object)
   */
  @Override
  protected void setParent(Object parent, Object child) {
    // do nothing
  }

  /**
   * {@inheritDoc}
   * @see groovy.util.BuilderSupport#nodeCompleted(java.lang.Object, java.lang.Object)
   */
  @Override
  protected void nodeCompleted(Object parent, Object node) {

  }
}