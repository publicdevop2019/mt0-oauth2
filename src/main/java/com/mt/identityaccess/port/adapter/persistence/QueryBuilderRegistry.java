package com.mt.identityaccess.port.adapter.persistence;

import com.mt.identityaccess.port.adapter.persistence.client.ClientQueryBuilder;
import com.mt.identityaccess.port.adapter.persistence.endpoint.EndpointQueryBuilder;
import com.mt.identityaccess.port.adapter.persistence.revoke_token.RevokeTokenQueryBuilder;
import com.mt.identityaccess.port.adapter.persistence.user.UpdateUserQueryBuilder;
import com.mt.identityaccess.port.adapter.persistence.user.UserQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderRegistry {
    private static ClientQueryBuilder clientSelectQueryBuilder;
    private static UserQueryBuilder userQueryBuilder;
    private static UpdateUserQueryBuilder updateUserQueryBuilder;
    private static EndpointQueryBuilder endpointQueryBuilder;
    private static RevokeTokenQueryBuilder revokeTokenQueryBuilder;

    public static RevokeTokenQueryBuilder revokeTokenQueryBuilder() {
        return revokeTokenQueryBuilder;
    }

    @Autowired
    public void setRevokeTokenQueryBuilder(RevokeTokenQueryBuilder revokeTokenQueryBuilder) {
        QueryBuilderRegistry.revokeTokenQueryBuilder = revokeTokenQueryBuilder;
    }

    @Autowired
    public void setEndpointQueryBuilder(EndpointQueryBuilder endpointQueryBuilder) {
        QueryBuilderRegistry.endpointQueryBuilder = endpointQueryBuilder;
    }

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

    public static EndpointQueryBuilder endpointSelectQueryBuilder() {
        return endpointQueryBuilder;
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
