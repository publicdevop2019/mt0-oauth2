package com.mt.identityaccess.port.adapter.persistence.revoke_token;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.revoke_token.RevokeTokenPaging;
import com.mt.identityaccess.application.revoke_token.RevokeTokenQuery;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import com.mt.identityaccess.domain.model.revoke_token.RevokeTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
@Primary
@Repository
public class RedisRevokeTokenRepository implements RevokeTokenRepository {
    private static final String REVOKE_TOKEN_PREFIX = "RT:";
    @Autowired
    private StringRedisTemplate redisTemplate;

    public SumPagedRep<RevokeToken> revokeTokensOfQuery(RevokeTokenQuery revokeTokenQuery, RevokeTokenPaging revokeTokenPaging, QueryConfig queryConfig) {
        return null;
    }

    public void add(RevokeToken revokeToken) {
        redisTemplate.opsForValue().set(REVOKE_TOKEN_PREFIX + revokeToken.getTargetId(), DomainRegistry.customObjectSerializer().serialize(revokeToken));
    }
}
