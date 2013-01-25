import grails.util.GrailsUtil
import com.jeffrick.grails.plugin.sharding.Shards
import com.jeffrick.grails.plugins.services.ShardService
import org.codehaus.groovy.grails.orm.hibernate.cfg.DefaultGrailsDomainConfiguration
import org.hibernate.tool.hbm2ddl.SchemaUpdate
import org.hibernate.cfg.Configuration
import com.jeffrick.grails.plugins.sharding.Shard
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext

class ShardingBootStrap {
    def ShardService shardService
    def private static Configuration _configuration
    def grailsApplication
    def dataSource

    def init = { servletContext ->

        Shard.withTransaction {
            Shards.list().each { shard ->

                // Make sure the shard is in the Shard table
                if (Shard.findByShardName(shard.name) == null) {
                    new Shard(shardName: shard.name, shardCapacity: 1000, shardUsage: 0, ratio: 0.0).save(flush: true)
                }

            }
        }


    }
    def destroy = {
    }
}