package com.hw.shared.idempotent;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static com.hw.shared.AppConstant.EXCHANGE_ROLLBACK;

@Slf4j
@Component
public class ChangeRecordSub {
    @Autowired
    private RootChangeRecordApplicationService rootChangeRecordApplicationService;
    @Autowired
    private AppChangeRecordApplicationService appChangeRecordApplicationService;
    @Value("${mq.queueName}")
    private String appQueueName;
    @Value("${mq.routingKey}")
    private String appRoutingKey;

    @PostConstruct
    public void initMQ() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_ROLLBACK, "direct");
            String queueName = channel.queueDeclare(appQueueName, true, false, false, null).getQueue();
            channel.queueBind(queueName, EXCHANGE_ROLLBACK, appRoutingKey);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                log.info("message received from mq");
                appChangeRecordApplicationService.deleteByQuery(message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

}
