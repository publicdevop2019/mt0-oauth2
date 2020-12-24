package com.mt.identityaccess.application;

import com.mt.identityaccess.config.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@EnableScheduling
@Component
public class EventApplicationService {
    @Autowired
    private EventPublisher eventPublisher;

    @Scheduled(fixedRateString = "${fixedRate.in.milliseconds.notification}")
    @Transactional
    public void publishEvents() {
        eventPublisher.publishNotifications();
    }

}
