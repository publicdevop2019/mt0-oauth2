package com.mt.common.application;

import com.mt.common.domain.model.DomainEvent;
import com.mt.common.domain.model.DomainEventPublisher;
import com.mt.common.domain.model.DomainEventSubscriber;
import com.mt.identityaccess.infrastructure.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@Slf4j
public class EventApplicationServiceAspectMonitor {
    @Autowired
    private EventRepository eventRepository;

    @Before("execution(* com.mt.identityaccess.application.client.ClientApplicationService.*(..))")
    public void listen() {
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<DomainEvent>() {
                    public void handleEvent(DomainEvent event) {
                        log.debug("append domain event {}", event);
                        eventRepository.append(event);
                    }
                    public Class<DomainEvent> subscribedToEventType() {
                        return DomainEvent.class; // all domain events
                    }
                });
    }
}
