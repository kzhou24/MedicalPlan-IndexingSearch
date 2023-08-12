package com.info7255.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *
 * </p>
 *
 * @author tangzb
 * @version 1.0
 * @className RestClientConfig
 * @since 2023/8/10 18:56
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 如果有多个从节点可以持续在内部new多个HttpHost，参数1是IP，参数2是端口，参数3是通信协议
        return new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")));
    }
}

