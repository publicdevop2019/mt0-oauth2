//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mt.identityaccess.port.adapter.messaging;

import com.mt.identityaccess.config.DomainEvent;
import com.mt.identityaccess.config.EventPublisher;
import com.mt.identityaccess.config.PublishedEventTracker;
import com.mt.identityaccess.config.PublishedEventTrackerRepository;
import com.mt.identityaccess.infrastructure.EventRepository;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.mt.identityaccess.port.adapter.messaging.RabbitMQEventSubscriber.EXCHANGE_NAME;

@Component
@Slf4j
public class RabbitMQEventPublisher implements EventPublisher {

    @Autowired
    private EventRepository eventStore;

    @Autowired
    private PublishedEventTrackerRepository publishedEventTrackerRepository;

    @Override
    public void publishNotifications() {
        PublishedEventTracker publishedNotificationTracker =
                publishedEventTrackerRepository.publishedNotificationTracker();
        List<DomainEvent> storedEvents =
                eventStore.allStoredEventsSince(publishedNotificationTracker.getLastPublishedEventId());
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            for (DomainEvent domainEvent : storedEvents) {
                byte[] notification2;
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    ObjectOutputStream out;
                    out = new ObjectOutputStream(bos);
                    out.writeObject(domainEvent);
                    out.flush();
                    notification2 = bos.toByteArray();
                }
                channel.basicPublish(EXCHANGE_NAME, "",
                        null,
                        notification2);
            }

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        publishedEventTrackerRepository
                .trackMostRecentPublishedNotification(
                        publishedNotificationTracker,
                        storedEvents);
    }
}
