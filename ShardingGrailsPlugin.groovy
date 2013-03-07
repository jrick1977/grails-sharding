import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsApplication

import com.jeffrick.grails.plugin.sharding.CurrentShard
import com.jeffrick.grails.plugin.sharding.ShardConfig
import com.jeffrick.grails.plugin.sharding.ShardEntityInterceptor
import com.jeffrick.grails.plugin.sharding.ShardingDS
import com.jeffrick.grails.plugin.sharding.Shards
import com.jeffrick.grails.plugin.sharding.annotation.Shard as ShardAnnotation
import com.jeffrick.grails.plugins.services.ShardService
import com.jeffrick.grails.plugins.sharding.Shard

class ShardingGrailsPlugin {
    def version = "0.8"
    def grailsVersion = "2.0.0 > *"
    def loadAfter = ['dataSource', 'domainClass', 'hibernate']
    def author = "Jeff Rick"
    def authorEmail = "jeffrick@gmail.com"
    def title = "Grails Shards Plugin"
    def description = 'Supports sharding of data'
    def documentation = "http://grails.org/plugin/sharding"

    def license = 'APACHE'
    def scm = [url: 'https://github.com/jrick1977/grails-sharding']
//    def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/???']
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]
    def developers = [ [ name: "Jeff Rick", email: "jeffrick@gmail.com" ]]

    def doWithSpring = {

        def shardDataSources = [:]

        shardDataSources.put(0, ref("dataSource"))
        int shardId = 1
        for (Map.Entry<String, Object> item in application.config.entrySet()) {
            if (item.key.startsWith('dataSource_')) {
                if (item.value.getProperty("shard")) {
                    shardDataSources.put(shardId++, ref(item.key))
                }
            }
        }
        
        Shards.shards = loadShardConfig(application)

        // Create the dataSource bean that has the Shard specific SwitchableDataSource implementation
        // we also set the targetDataSoures map to the one we built above
        dataSource(ShardingDS) {
            targetDataSources = shardDataSources
        }

        // Define an entityInterceptor that will be triggered when transactions
        // start and end.  This basically coordinates the starting and stopping of transactions
        // between the active shard and the index database
        entityInterceptor(ShardEntityInterceptor)
    }

    def doWithDynamicMethods = { ctx ->

        // Find the domain class the owning application has defined as
        // the "Index" domain class.  This domain class is used to store
        // the list of objects and the shard they live in
        application.domainClasses.each {
            DefaultGrailsDomainClass domainClass ->

                if (domainClass.clazz.isAnnotationPresent(ShardAnnotation)) {

                    // For the index domain class add a beforeInsert event handler
                    // that will assign the next shard to the object being saved.
                    // In the future will need to be able to chain this event with existing beforeInsert
                    // event handlers
                    domainClass.metaClass.beforeInsert = {->
                        ShardAnnotation prop = domainClass.clazz.getAnnotation(ShardAnnotation)
                        ShardService shardService = ctx.shardService

                        // Before we insert we need to figure out the shard to assign ourselves to
                        def shardObject = shardService.getNextShard()
                        shardObject.refresh()

                        // Set the shard on the object
                        String fieldName = prop.fieldName()
                        if (!delegate."$fieldName") {
                            delegate."$fieldName" = shardObject.shardName

                            // Increment the usage of the shard assigned
                            Shard.withNewSession {
                                shardObject.refresh()
                                shardObject.incrementUsage()
                            }

                            shardObject.refresh()
                        }

                        return true
                    }
                }
        }
    }

    private loadShards(GrailsApplication app) {
        try {
            def shards = [:]
            int shardId = 1
		app.config.each { key, value ->
                if (key.startsWith('dataSource_')) {
                    shards.put(shardId++, ref(key))
                }
            }
            return shards
        }
        catch (e) {
            println e.message
            e.printStackTrace()
            return [:]
        }
    }

    private loadShardConfig(GrailsApplication app) {
        try {
            def shards = []
            def dataSourceLookup = [:]
            int shardId = 1
		app.config.each { key, value ->
                if (key.startsWith('dataSource_')) {
                    if (value.getProperty("shard")) {
                        ShardConfig shardConfig = new ShardConfig()
                        shardConfig.id = shardId++
                        shardConfig.name = key.replace("dataSource_", "")
                        shards.add(shardConfig)

                    }
                    dataSourceLookup.put(key, value)
                }
            }

            CurrentShard.setDataSourceLookup(dataSourceLookup)
            return shards
        }
        catch (e) {
            println e.message
            e.printStackTrace()
            return []
        }
    }
}
