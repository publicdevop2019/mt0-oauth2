package com.mt.identityaccess.port.adapter.messaging;

import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeoutException;

import static com.mt.common.CommonConstant.EXCHANGE_NAME;

@Slf4j
@Component
public class DomainEventMQSubscriber {
    private static final String TASK_QUEUE_NAME = "domain_event_queue";

    static {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            channel.queueBind(TASK_QUEUE_NAME, EXCHANGE_NAME, "");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                ByteArrayInputStream bis = new ByteArrayInputStream(delivery.getBody());
                Object o = null;
                try (ObjectInput in = new ObjectInputStream(bis)) {
                    o = in.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                log.debug("received message from mq");
                ApplicationServiceRegistry.clientApplicationService().revokeTokenBasedOnChange(o);
                ApplicationServiceRegistry.userApplicationService().revokeTokenBasedOnChange(o);
                ApplicationServiceRegistry.endpointApplicationService().reloadInProxy(o);
            };
            channel.basicConsume(TASK_QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            log.error("error in mq", e);
        }
    }
}
