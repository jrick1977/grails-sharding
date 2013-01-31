package com.jeffrick.grails.plugin.sharding

import org.hibernate.EmptyInterceptor
import org.hibernate.Transaction
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.orm.hibernate3.HibernateTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.AbstractPlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

/**
 * Synchronizes the transaction in the shard database with the one in the index database.
 * @author <a href='mailto:jeffrick@gmail.com'>Jeff Rick</a>
 */
class ShardEntityInterceptor extends EmptyInterceptor implements ApplicationContextAware {
  Transaction indexTransaction
  HibernateTransactionManager trnsManager
  TransactionStatus trnsStatus
  def transactionManager
  ApplicationContext applicationContext

  void afterTransactionBegin(Transaction transaction) {
    String trnsDatabase = transaction.jdbcContext.connectionManager.connection.getMetaData().getURL()
    String indexDatabaseURL= CurrentShard.getIndexDatabaseURL()

    // If the database starting a transaction isn't the index database then we need to start a transaction
    // for the index database
    if (!trnsDatabase.equals(indexDatabaseURL)) {

      // Get the transaction manager for the index database
      trnsManager = applicationContext.getBean("transactionManager_" + CurrentShard.getIndexDataSourceName().replace("dataSource_",""))
      trnsManager.setTransactionSynchronization AbstractPlatformTransactionManager.SYNCHRONIZATION_NEVER

      // Create a new transaction and store it for later use
      TransactionDefinition transDef = new DefaultTransactionDefinition()
      trnsStatus = trnsManager.getTransaction(transDef)
    }
  }

  void afterTransactionCompletion(Transaction transaction) {
    // Handle the completion side of the transaction
    // if the transaction was committed then we need to commit the index
    // transaction otherwise we should rollback
    if (transaction?.wasCommitted()) {
      if (!trnsStatus?.completed) {
        trnsManager?.commit(trnsStatus)
      }
    } else {
      if (!trnsStatus?.completed) {
        trnsManager?.rollback(trnsStatus)
      }
    }

    super.afterTransactionCompletion(transaction)
  }
}
