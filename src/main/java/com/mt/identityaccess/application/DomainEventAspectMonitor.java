package com.mt.identityaccess.application;

import com.mt.identityaccess.config.DomainEvent;
import com.mt.identityaccess.config.DomainEventPublisher;
import com.mt.identityaccess.config.DomainEventSubscriber;
import com.mt.identityaccess.infrastructure.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@Slf4j
public class DomainEventAspectMonitor {
    @Autowired
    private EventRepository eventRepository;

    @Before("execution(* com.mt.identityaccess.application.client.ClientApplicationService.*(..))")
    public void listen() {
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<DomainEvent>() {
                    public void handleEvent(DomainEvent event) {
                        log.debug("domain event received " + event.toString());
                        eventRepository.append(event);
                    }
                    public Class<DomainEvent> subscribedToEventType() {
                        return DomainEvent.class; // all domain events
                    }
                });
    }
}
