package com.mt.common.application;

import com.mt.common.domain.model.DomainEvent;
import com.mt.common.domain.model.DomainEventPublisher;
import com.mt.common.domain.model.DomainEventSubscriber;
import com.mt.common.domain.model.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@Slf4j
public class SubscribeForEventAspectConfig {
    @Autowired
    private EventRepository eventRepository;

    @Pointcut("@annotation(com.mt.common.application.SubscribeForEvent)")
    public void listen() {
        //for aop purpose
    }

    @Before(value = "com.mt.common.application.SubscribeForEventAspectConfig.listen()")
    public void around(JoinPoint joinPoint) throws Throwable {
        log.debug("subscribe for event change {}", joinPoint.getSignature().toShortString());
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
