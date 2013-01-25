import com.jeffrick.grails.plugin.sharding.CurrentShard
import com.jeffrick.grails.plugin.sharding.ShardConfig
import com.jeffrick.grails.plugin.sharding.Shards
import com.jeffrick.grails.plugin.sharding.annotation.Shard
import com.jeffrick.grails.plugin.sharding.ShardingDS
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import com.jeffrick.grails.plugins.services.ShardService
import com.jeffrick.grails.plugin.sharding.ShardEntityInterceptor
import org.codehaus.groovy.grails.commons.GrailsApplication
import java.util.Map.Entry

class ShardingGrailsPlugin {
    def version = "0.7"
    def grailsVersion = "2.2.0 > *"
    def dependsOn = [:]
    def loadAfter = ['dataSource', 'domainClass', 'hibernate']
    def pluginExcludes = ["grails-app/views/error.gsp"]
    def author = "Jeff Rick"
    def authorEmail = "jeffrick@gmail.com"
    def title = "Grails Shards Plugin"
    def description = 'Supports sharding of data'
    def documentation = "http://grails.org/plugin/sharding"


    def doWithSpring = {


        def shardDataSources = [:]

        shardDataSources.put(0, ref("dataSource"))
        int shardId = 1
        for (Entry<String, Object> item in application.config.entrySet()) {

            if (item.key.startsWith('dataSource_')) {
                shardDataSources.put(shardId++, ref(item.key))
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


    def doWithWebDescriptor = { xml -> }

    def doWithDynamicMethods = { ctx ->

        // Find the domain class the owning application has defined as
        // the "Index" domain class.  This domain class is used to store
        // the list of objects and the shard they live in
        application.domainClasses.each {
            DefaultGrailsDomainClass domainClass ->

                if (domainClass.clazz.isAnnotationPresent(Shard)) {

                    // For the index domain class add a beforeInsert event handler
                    // that will assign the next shard to the object being saved.
                    // In the future will need to be able to chain this event with existing beforeInsert
                    // event handlers
                    domainClass.metaClass.beforeInsert {->
                        def prop = domainClass.clazz.getAnnotation(Shard)
                        ShardService shardService = ctx.getBean('shardService')

                        // Before we insert we need to figure out the shard to assign ourselves to
                        def shardObject = shardService.getNextShard()
                        shardObject.refresh()

                        // Set the shard on the object
                        def fieldName = prop.fieldName()
                        if (delegate."$fieldName" == null || delegate."$fieldName" == "") {
                            print(shardObject)
                            delegate."$fieldName" = shardObject.shardName

                            // Increment the usage of the shard assigned
                            com.jeffrick.grails.plugins.sharding.Shard.withNewSession {
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

    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }

    private loadShards(GrailsApplication app) {
        try {
            def shards = [:]
            int shardId = 1
            for (Entry<String, Object> item in app.config.entrySet()) {

                if (item.key.startsWith('dataSource_')) {
                    shards.put(shardId++, ref(item.key))
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
            for (Entry<String, ConfigObject> item in app.config.entrySet()) {

                if (item.key.startsWith('dataSource_')) {
                    if (item.value.getProperty("shard")) {
                        ShardConfig shardConfig = new ShardConfig()
                        shardConfig.id = shardId++
                        shardConfig.name = item.key.replace("dataSource_", "")
                        shards.add(shardConfig)

                    }
                    dataSourceLookup.put(item.key, item.value)
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
