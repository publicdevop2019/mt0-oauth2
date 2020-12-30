package com.mt.identityaccess.port.adapter.persistence;

import com.mt.identityaccess.port.adapter.persistence.client.ClientQueryBuilder;
import com.mt.identityaccess.port.adapter.persistence.user.UpdateUserQueryBuilder;
import com.mt.identityaccess.port.adapter.persistence.user.UserQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderRegistry {
    private static ClientQueryBuilder clientSelectQueryBuilder;
    private static UserQueryBuilder userQueryBuilder;
    private static UpdateUserQueryBuilder updateUserQueryBuilder;

    @Autowired
    public void setClientQueryBuilder(ClientQueryBuilder clientSelectQueryBuilder) {
        QueryBuilderRegistry.clientSelectQueryBuilder = clientSelectQueryBuilder;
    }

    @Autowired
    public void setUpdateUserQueryBuilder(UpdateUserQueryBuilder userUpdateQueryBuilder) {
        QueryBuilderRegistry.updateUserQueryBuilder = userUpdateQueryBuilder;
    }

    public static ClientQueryBuilder clientSelectQueryBuilder() {
        return clientSelectQueryBuilder;
    }

    @Autowired
    public void setUserQueryBuilder(UserQueryBuilder userQueryBuilder) {
        QueryBuilderRegistry.userQueryBuilder = userQueryBuilder;
    }

    public static UserQueryBuilder userQueryBuilder() {
        return userQueryBuilder;
    }

    public static UpdateUserQueryBuilder updateUserQueryBuilder() {
        return updateUserQueryBuilder;
    }
}
