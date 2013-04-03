package com.jeffrick.grails.plugin.sharding

import org.hibernate.EmptyInterceptor
import org.hibernate.Transaction
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.AbstractPlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

/**
 * Synchronizes the transaction in the shard database with the one in the index database.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 */
class ShardEntityInterceptor extends EmptyInterceptor implements ApplicationContextAware {
    ApplicationContext applicationContext

    void afterTransactionBegin(Transaction transaction) {
        String trnsDatabase = transaction.jdbcContext.connectionManager.connection.getMetaData().getURL()
        String indexDatabaseURL = CurrentShard.getIndexDatabaseURL()

        // If the database starting a transaction isn't the index database then we need to start a transaction
        // for the index database
        if (!trnsDatabase.equals(indexDatabaseURL) && CurrentShard.getTransactionStatus(trnsDatabase) == null) {

            // Get the transaction manager for the index database
            AbstractPlatformTransactionManager trnsManager = applicationContext.getBean("transactionManager_" + CurrentShard.getIndexDataSourceName().replace("dataSource_", ""))
            trnsManager.setTransactionSynchronization AbstractPlatformTransactionManager.SYNCHRONIZATION_NEVER
            CurrentShard.setTransactionManager(trnsManager)

            // Create a new transaction and store it for later use
            TransactionDefinition transDef = new DefaultTransactionDefinition()

            // Set the transaction status
            CurrentShard.setTransactionStatus(trnsDatabase, trnsManager.getTransaction(transDef))
        }
    }

    void afterTransactionCompletion(Transaction transaction) {
        // Handle the completion side of the transaction
        // if the transaction was committed then we need to commit the index
        // transaction otherwise we should rollback
        if (transaction?.wasCommitted()) {
            for (TransactionStatus trnsStatus : CurrentShard.getTransactionStatus()) {
                if (!trnsStatus?.completed) {
                    CurrentShard.getTransactionManager()?.commit(trnsStatus)
                }
            }
        } else {
            for (TransactionStatus trnsStatus : CurrentShard.getTransactionStatus()) {
                if (!trnsStatus?.completed) {
                    CurrentShard.getTransactionManager()?.rollback(trnsStatus)
                }
            }
        }

        CurrentShard.clearTransactionStatus()

        super.afterTransactionCompletion(transaction)
    }
}
