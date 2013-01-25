package com.jeffrick.grails.plugin.sharding

/**
 * Created by IntelliJ IDEA.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 * Date: Jun 16, 2010
 * Time: 4:58:33 PM
 */
class ShardBuilder extends BuilderSupport {
  def _currentShardName
  def shards = []
  private ShardConfig _currentShard

  /**
   * {@inheritDoc}
   * @see groovy.util.BuilderSupport#createNode(java.lang.Object)
   */
  @Override
  protected Object createNode(Object name) {
    _currentShardName = name
    _currentShard = new ShardConfig()
  }

  /**
   * {@inheritDoc}
   * @see groovy.util.BuilderSupport#createNode(java.lang.Object, java.lang.Object)
   */
  @Override
  protected Object createNode(Object name, Object value) {

    switch(name) {
      case "name":
      case "jdbcUrl":
      case "user":
      case "password":
      case "driverClass":
      case "capacity":
      case "autoCommit":
      case "initialPoolSize":
      case "minPoolSize":
      case "maxPoolSize":
      case "testConnectionOnCheckin":
      case "testConnectionOnCheckout":
      case "maxIdleTime":
      case "maxConnectionAge":
      case "automaticTestTable":
        _currentShard."$name" = value
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

    if ('shard' == name) {
      _currentShard = attributes
      shards.add(_currentShard)
      return name
    }

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
       if(node.class  == ShardConfig.class ) {
         shards.add(_currentShard)
       }
  }
}
