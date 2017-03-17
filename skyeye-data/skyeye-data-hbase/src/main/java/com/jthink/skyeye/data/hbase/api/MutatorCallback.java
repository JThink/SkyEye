package com.jthink.skyeye.data.hbase.api;

import org.apache.hadoop.hbase.client.BufferedMutator;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc callback for hbase put delete and update
 * @date 2016-12-08 14:31:34
 */
public interface MutatorCallback {

    /**
     * 使用mutator api to update put and delete
     * @param mutator
     * @throws Throwable
     */
    void doInMutator(BufferedMutator mutator) throws Throwable;
}
