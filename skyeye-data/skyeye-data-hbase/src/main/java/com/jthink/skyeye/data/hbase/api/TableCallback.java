package com.jthink.skyeye.data.hbase.api;

import org.apache.hadoop.hbase.client.Table;

/**
 * Callback interface for Hbase code. To be used with {@link HbaseTemplate}'s execution methods, often as anonymous classes within a method implementation without
 * having to worry about exception handling.
 *
 * @author Costin Leau
 */
/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc copy from spring data hadoop hbase, modified by JThink, use the 1.0.0 api
 * @date 2016-11-15 14:49:52
 */
public interface TableCallback<T> {

    /**
     * Gets called by {@link HbaseTemplate} execute with an active Hbase table. Does need to care about activating or closing down the table.
     *
     * @param table active Hbase table
     * @return a result object, or null if none
     * @throws Throwable thrown by the Hbase API
     */
    T doInTable(Table table) throws Throwable;
}