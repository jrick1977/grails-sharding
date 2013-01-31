import com.jeffrick.grails.plugin.sharding.Shards
import com.jeffrick.grails.plugins.sharding.Shard

class ShardingBootStrap {
    def init = { servletContext ->

        Shard.withTransaction {
            Shards.list().each { shard ->

                // Make sure the shard is in the Shard table
                if (!Shard.findByShardName(shard.name)) {
                    new Shard(shardName: shard.name, shardCapacity: 1000, shardUsage: 0, ratio: 0.0).save()
                }
            }
        }
    }
}
