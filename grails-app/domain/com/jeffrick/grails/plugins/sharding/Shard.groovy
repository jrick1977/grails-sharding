package com.jeffrick.grails.plugins.sharding

/**
 * Manages all of the database shards within the system.  This table is primarily
 * used to find the next shard to assing a new user to.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 */
class Shard {
  static mapping = {
      cache true
      datasources(['index'])
  }

  /**
   * The name of the shard, used internally to refer to the shard.
   */
  String shardName
  /**
   * The number of objects (either RSS feeds or Users depending on the type) that this shard can contain.
   */
  Integer shardCapacity
  /**
   * The number of objects (either RSS feeds or Users depending on the type) that this shard currently holds.
   */
  Integer shardUsage
  /**
   * The percentage of the capacity that this shard contains.  This column is used to find the shard with the lowest ratio
   * and assign the new user to.
   */
  Double ratio

  /**
   * The date this record was created;
   */
  Date dateCreated
  /**
   * The date this record was last updated.
   */
  Date lastUpdated

  static constraints = {
    lastUpdated(nullable: true)
  }

  def incrementUsage() {
    // Increment usage
    shardUsage++

    // Reset the ratio
    ratio = shardUsage / shardCapacity

    // Save the changes
    save(flush: true)
  }

  String toString() {
    "com.rezzonate.domain.index.Shard{id=$id, shardName=$shardName, shardUsage='$shardUsage', ratio='$ratio'}"
  }
}
