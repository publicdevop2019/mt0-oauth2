package com.hw.application;

import com.hw.config.DomainEvent;
import com.hw.config.DomainEventPublisher;
import com.hw.config.DomainEventSubscriber;
import com.hw.infrastructure.EventStore;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
@Slf4j
public class IdentityAccessEventProcessor {
    @Autowired
    private EventStore eventStore;

    public static void register() {
        (new IdentityAccessEventProcessor()).listen();
    }

    @Before("execution(* com.mt.identityaccess.application.*.*(..))")
    public void listen() {
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<DomainEvent>() {

                    public void handleEvent(DomainEvent aDomainEvent) {
                        log.info("domain event received "+aDomainEvent.toString());
                        store(aDomainEvent);
                    }

                    public Class<DomainEvent> subscribedToEventType() {
                        return DomainEvent.class; // all domain events
                    }
                });
    }

    private void store(DomainEvent aDomainEvent) {
        this.eventStore().append(aDomainEvent);
    }

    private EventStore eventStore() {
        return this.eventStore;
    }
}
