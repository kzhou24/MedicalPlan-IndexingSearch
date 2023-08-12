package com.info7255.service;

/**
 * <p>
 *
 * </p>
 *
 * @author tangzb
 * @version 1.0
 * @className MessageSender
 * @since 2023/8/10 22:12
 */

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageSender {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    /**
     * @param message
     * @param status  1 新增 2修改
     */
    public void sendMessage(String message, String status) {
        Map map = new HashMap<>();
        map.put("message", message);
        map.put("status", "2");
        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);
//        rabbitTemplate.convertAndSend("indexChannel", message);
    }
}


