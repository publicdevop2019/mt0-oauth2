package com.hw.port.adapter.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SelectQueryBuilderRegistry {
    private static ClientSelectQueryBuilder clientSelectQueryBuilder;

    @Autowired
    public void setClientSelectQueryBuilder(ClientSelectQueryBuilder clientSelectQueryBuilder) {
        SelectQueryBuilderRegistry.clientSelectQueryBuilder = clientSelectQueryBuilder;
    }

    public static ClientSelectQueryBuilder clientSelectQueryBuilder() {
        return clientSelectQueryBuilder;
    }
}
