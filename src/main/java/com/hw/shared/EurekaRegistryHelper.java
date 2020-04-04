package com.hw.shared;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EurekaRegistryHelper {
    @Autowired
    EurekaClient eurekaClient;

    public String getProxyHomePageUrl() {
        InstanceInfo proxy = eurekaClient.getNextServerFromEureka("PROXY", false);
        log.info("proxy url retrieved from eureka client {}", proxy.getHomePageUrl());
        return proxy.getHomePageUrl();
    }
}
