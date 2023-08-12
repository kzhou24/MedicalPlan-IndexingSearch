package com.info7255.config;

/**
 * <p>
 *
 * </p>
 *
 * @author tangzb
 * @version 1.0
 * @className MessageReceiver
 * @since 2023/8/10 22:59
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.info7255.service.ElasticsearchService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RabbitListener(queues = "TestDirectQueue")//监听的队列名称 TestDirectQueue
public class DirectReceiver {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @RabbitHandler
    public void process(Map testMessage) throws IOException {
        String message = testMessage.get("message").toString();
        String status = testMessage.get("status").toString();
        System.out.println("message->" + message);
        if ("2".equals(status)) {
            JSONObject jsonObject = JSONObject.parseObject(message);
            elasticsearchService.update(jsonObject.get("objectId").toString(), message);
        }
        elasticsearchService.writeDataToElasticsearch("", message);
    }

}