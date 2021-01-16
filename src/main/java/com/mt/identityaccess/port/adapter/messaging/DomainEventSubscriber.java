package com.mt.identityaccess.port.adapter.messaging;

import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.domain.DomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.mt.identityaccess.domain.model.client.event.ClientEvent.TOPIC_CLIENT;
import static com.mt.identityaccess.domain.model.user.event.UserEvent.TOPIC_USER;

@Slf4j
@Component
public class DomainEventSubscriber {
    private static final String CLIENT_QUEUE_NAME = "client_queue";
    private static final String TOKEN_QUEUE_NAME = "token_queue";
    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    private void clientListener() {
        DomainRegistry.eventStreamService().subscribe(appName, true, CLIENT_QUEUE_NAME, (event) -> {
            ApplicationServiceRegistry.clientApplicationService().handleChange(event);
        }, TOPIC_CLIENT);
    }

    @PostConstruct
    private void tokenListener() {
        DomainRegistry.eventStreamService().subscribe(appName, true, TOKEN_QUEUE_NAME, (event) -> {
            ApplicationServiceRegistry.revokeTokenApplicationService().handleChange(event);
        }, TOPIC_USER, TOPIC_CLIENT);
    }

}
