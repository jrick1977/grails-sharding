package com.jeffrick.grails.plugin.sharding.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface Shard {
    String fieldName()
    String indexDataSourceName()
}