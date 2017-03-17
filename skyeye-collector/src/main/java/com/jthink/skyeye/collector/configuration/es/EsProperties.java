package com.jthink.skyeye.collector.configuration.es;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc es的配置项
 * @date 2016-09-20 10:44:54
 */
@ConfigurationProperties(prefix = "spring.indexer.es")
public class EsProperties {

    private String ips;

    private String cluster;

    private int port;

    private boolean sniff;

    private String index;

    private String doc;

    private String indexEvent;

    private String docEvent;

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSniff() {
        return sniff;
    }

    public void setSniff(boolean sniff) {
        this.sniff = sniff;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getIndexEvent() {
        return indexEvent;
    }

    public void setIndexEvent(String indexEvent) {
        this.indexEvent = indexEvent;
    }

    public String getDocEvent() {
        return docEvent;
    }

    public void setDocEvent(String docEvent) {
        this.docEvent = docEvent;
    }
}
