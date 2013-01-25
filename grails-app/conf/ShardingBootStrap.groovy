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

    def init = {servletContext ->

        Shards.list().each {shard ->
            shardService.change(shard)

            def conn = dataSource.getConnection()

            Properties properties = new Properties()
            properties.setProperty 'hibernate.connection.driver_class', shard.driverClass
            properties.setProperty 'hibernate.connection.username', shard.user
            properties.setProperty 'hibernate.connection.password', shard.password
            properties.setProperty 'hibernate.connection.url', shard.jdbcUrl
            properties.setProperty 'hibernate.dialect', 'org.hibernate.dialect.MySQLDialect'

            _configuration = new DefaultGrailsDomainConfiguration(
                    grailsApplication: grailsApplication,
                    properties: properties)

            // rebuild the database before each test.
            ApplicationContext applicationContext = servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
            def sessionFactoryBean = applicationContext.getBean("&sessionFactory")
            sessionFactoryBean.updateDatabaseSchema()

            _configuration = null

            // Make sure the shard is in the Shard table
            if (Shard.findByShardName(shard.name) == null) {
                new Shard(shardName: shard.name, shardCapacity: shard.capacity, shardUsage: 0, ratio: 0.0).save(flush: true)
            }

        }
    }
    def destroy = {
    }
}