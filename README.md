grails-sharding
===============

Grails Sharding Plugin

Dependency : Grails 2.0.0+

Documentation

Description
The Sharding plugin allows application programmers to define multiple database shards to horizontally scale their user's data across multiple database schema and servers. The programmer can easily change the database the set of Domain Classes are targeting with a simple service call and objects used to segment the data are assigned a shard based on all shards usage and capacity. This is not available to grails programs out of the box as while there is more than one DataSource and SessionFactory there is no way to switch the default DataSource based on runtime behavior.

Dependancies

The Sharding plugin follows the convention over configuration philosiphy of grails but in some cases needs the programmer to provide some configuration. This configuration is in two places.  First you have to define all of the data sources your application will use in the normal grails-app/conf/DataSource.groovy.  In here you should have at least four data sources:

     -- Default Datasource
     -- Index Datasource - should be the same database as defined for the
        default.
     -- Shard Data Source
     -- Shard Data Source
     -- Shard ....
     -- etc

In addition to the normal DSL for multiple data sources there is one additional configuration value you must add, the shard attribute.  This defines whether this datasource is a shard that should be included as we assign shards to objects.

Note: You MUST have a DataSource named datasource_index where in the index object will live

Here is an example DataSource.groovy

	dataSource {
		pooled = true
		driverClassName = "org.h2.Driver"
		username = "sa"
		password = ""
	}
	hibernate {
		cache.use_second_level_cache=true
		cache.use_query_cache=false
		cache.provider_class='net.sf.ehcache.hibernate.EhCacheProvider'
	}

	// environment specific settings
	environments {
		development {
			dataSource {
				shard = false
				dbCreate = "update" // one of 'create', 'create-drop','update'
				url = "jdbc:mysql://localhost:3306/shardINDEX"
				username = "root"
			}
			dataSource_index {
				shard = false
				dbCreate = "update" // one of 'create', 'create-drop','update'
				url = "jdbc:mysql://localhost:3306/shardINDEX"
				username = "root"
			}
			dataSource_shard1001 {
				shard = true
				dbCreate = "update" // one of 'create', 'create-drop','update'
				url = "jdbc:mysql://localhost:3306/shard1001"
				username = "root"
			}
			dataSource_shard1002 {
				shard = true
				dbCreate = "update" // one of 'create', 'create-drop','update'
				url = "jdbc:mysql://localhost:3306/shard1002"
				username = "root"
			}
		}
		test {
			...
		}
		production {
			...
		}
	}

In addition to the DataSource.groovy file you must also create a domain class that will be used to associate data with a shard.  Most often this class is a user class but could be any thing you would like.  Once you have created that domain class you need to do the following:

           -- Create a string field that will hold the shard assignment (typically
              called shard)
           -- Set the datasources attribute for the class to be 'index'
           -- Add the com.jeffrick.grails.plugin.sharding.annotation.Shard
              annotation to that class
           -- Create a fieldName and indexDataSourceName attributes of the
              annotation

Here is an example class (UserIndex.groovy):

	import com.jeffrick.grails.plugin.sharding.annotation.Shard

	@Shard(fieldName = "shard", indexDataSourceName = "dataSource_index")
	class UserIndex {

		String userName

		String shard

		static mapping = {
			datasources(['index'])
		}
	}

Domain Classes that should be sharded
Finally any Domain Class that you want to be sharded you must set to use the ALL datasource to do this add the following to your domain class:
	static mapping = {
		datasource 'ALL'
	}


Shard Domain Class
The plugin creates a Domain Class called Shard which contains a record for every shard in the system. Included in this Domain Class is the capacity of the shard and the number of objects assigned to the shard. This class is queried every time a new object (typically a user) is created within the system to assign a shard based on the usage to capacity ratio. This allows for shards to be located on different types of hardware that should take either a more or less objects.

Usage
To install the plugin in your application add a dependency in the "plugins" section of your BuildConfig.groovy, e.g.

   compile ':sharding:0.7'

but be sure to replace "0.7" with the latest version.

Authors

Jeff Rick jeffrick@gmail.com
Robert Woollam

Please report any issues to the Grails User mailing list and/or write up an issue in JIRA at http://jira.codehaus.org/browse/GRAILSPLUGINS under the Grails-Sharding component.
