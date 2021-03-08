package com.mt.identityaccess.port.adapter.persistence;

import com.mt.identityaccess.port.adapter.persistence.client.SpringDataJpaClientRepository;
import com.mt.identityaccess.port.adapter.persistence.endpoint.SpringDataJpaEndpointRepository;
import com.mt.identityaccess.port.adapter.persistence.revoke_token.RedisRevokeTokenRepository;
import com.mt.identityaccess.port.adapter.persistence.user.SpringDataJpaUserRepository;
import com.mt.identityaccess.port.adapter.persistence.user.UpdateUserQueryBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderRegistry {
    @Getter
    private static SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor clientSelectQueryBuilder;
    @Getter
    private static SpringDataJpaUserRepository.JpaCriteriaApiUserAdaptor userQueryBuilder;
    @Getter
    private static UpdateUserQueryBuilder updateUserQueryBuilder;
    @Getter
    private static RedisRevokeTokenRepository.RedisRevokeTokenAdaptor redisRevokeTokenAdaptor;
    @Getter
    private static SpringDataJpaEndpointRepository.JpaCriteriaApiEndpointAdapter endpointQueryBuilder;


    @Autowired
    public void setEndpointQueryBuilder(SpringDataJpaEndpointRepository.JpaCriteriaApiEndpointAdapter endpointQueryBuilder) {
        QueryBuilderRegistry.endpointQueryBuilder = endpointQueryBuilder;
    }

    @Autowired
    public void setRevokeTokenAdaptor(RedisRevokeTokenRepository.RedisRevokeTokenAdaptor redisRevokeTokenAdaptor) {
        QueryBuilderRegistry.redisRevokeTokenAdaptor = redisRevokeTokenAdaptor;
    }

    @Autowired
    public void setClientQueryBuilder(SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor clientSelectQueryBuilder) {
        QueryBuilderRegistry.clientSelectQueryBuilder = clientSelectQueryBuilder;
    }

    @Autowired
    public void setUpdateUserQueryBuilder(UpdateUserQueryBuilder userUpdateQueryBuilder) {
        QueryBuilderRegistry.updateUserQueryBuilder = userUpdateQueryBuilder;
    }

    @Autowired
    public void setUserQueryBuilder(SpringDataJpaUserRepository.JpaCriteriaApiUserAdaptor userQueryBuilder) {
        QueryBuilderRegistry.userQueryBuilder = userQueryBuilder;
    }
}
