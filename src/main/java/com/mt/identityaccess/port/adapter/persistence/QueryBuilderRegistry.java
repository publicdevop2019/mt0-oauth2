package com.mt.identityaccess.port.adapter.persistence;

import com.mt.identityaccess.port.adapter.persistence.client.ClientQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderRegistry {
    private static ClientQueryBuilder clientSelectQueryBuilder;

    @Autowired
    public void setClientSelectQueryBuilder(ClientQueryBuilder clientSelectQueryBuilder) {
        QueryBuilderRegistry.clientSelectQueryBuilder = clientSelectQueryBuilder;
    }

    public static ClientQueryBuilder clientSelectQueryBuilder() {
        return clientSelectQueryBuilder;
    }
}
