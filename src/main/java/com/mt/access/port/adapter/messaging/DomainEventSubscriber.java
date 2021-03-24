package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.access.domain.model.client.event.ClientEvent.TOPIC_CLIENT;
import static com.mt.access.domain.model.user.event.UserEvent.TOPIC_USER;

@Slf4j
@Component
public class DomainEventSubscriber {
    private static final String CLIENT_QUEUE_NAME = "client_queue";
    private static final String TOKEN_QUEUE_NAME = "token_queue";
    private static final String EP_QUEUE_NAME = "ep_queue";
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void clientListener() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, CLIENT_QUEUE_NAME, (event) -> {
            ApplicationServiceRegistry.clientApplicationService().handleChange(event);
        }, TOPIC_CLIENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void tokenListener() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, TOKEN_QUEUE_NAME, (event) -> {
            ApplicationServiceRegistry.revokeTokenApplicationService().handleChange(event);
        }, TOPIC_USER, TOPIC_CLIENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void epListener() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, EP_QUEUE_NAME, (event) -> {
            ApplicationServiceRegistry.endpointApplicationService().handleChange(event);
        }, TOPIC_CLIENT);
    }

}
