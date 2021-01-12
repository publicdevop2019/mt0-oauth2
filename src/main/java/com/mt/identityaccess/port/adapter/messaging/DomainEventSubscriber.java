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
    private static final String USER_QUEUE_NAME = "user_queue";
    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    private void listenClient() {
        DomainRegistry.eventStreamService().subscribe(appName, true,CLIENT_QUEUE_NAME, TOPIC_CLIENT, (event) -> {
            ApplicationServiceRegistry.clientApplicationService().revokeTokenBasedOnChange(event);
        });
    }

    @PostConstruct
    private void listenUser() {
        DomainRegistry.eventStreamService().subscribe(appName, true,USER_QUEUE_NAME, TOPIC_USER, (event) -> {
            ApplicationServiceRegistry.userApplicationService().revokeTokenBasedOnChange(event);
        });
    }

}
