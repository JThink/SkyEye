package com.jthink.skyeye.collector.core.configuration.es;

import com.jthink.skyeye.base.constant.Constants;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc elasticsearch 配置
 * @date 2016-09-20 09:13:32
 */
@ConditionalOnClass({ Settings.class, TransportClient.class })
@Configuration
@EnableConfigurationProperties(EsProperties.class)
public class EsConfiguration {

    private static Logger LOGGER = LoggerFactory.getLogger(EsConfiguration.class);

    @Autowired
    private EsProperties esProperties;

    @Bean
    public Settings settings() {
        Settings settings = Settings.settingsBuilder().put("cluster.name", this.esProperties.getCluster())
                .put("client.transport.sniff", this.esProperties.isSniff()).build();

        return settings;
    }

    @Bean
    public TransportClient transportClient(Settings settings) {
        TransportClient client = TransportClient.builder().settings(settings).build();
        for (String ip : this.esProperties.getIps().split(Constants.COMMA)) {
            try {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), this.esProperties.getPort()));
            } catch (UnknownHostException e) {
                LOGGER.error("es集群主机名错误, ip: {}", ip);
            }
        }
        return client;
    }

}
