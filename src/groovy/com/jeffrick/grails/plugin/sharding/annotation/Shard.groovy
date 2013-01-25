package com.jeffrick.grails.plugin.sharding.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 1/16/13
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Shard {
    String fieldName();
    String indexDataSourceName();
}