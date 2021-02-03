package com.mt.identityaccess.port.adapter.persistence.revoke_token;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.revoke_token.RevokeTokenQuery;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import com.mt.identityaccess.domain.model.revoke_token.RevokeTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Repository
public class RedisRevokeTokenRepository implements RevokeTokenRepository {
    private static final String REVOKE_TOKEN_PREFIX = "RT:";
    @Autowired
    private StringRedisTemplate redisTemplate;

    public SumPagedRep<RevokeToken> revokeTokensOfQuery(RevokeTokenQuery query, DefaultPaging paging, QueryConfig queryConfig) {
        List<RevokeToken> revokeTokens = new ArrayList<>();
        SumPagedRep<RevokeToken> revokeTokenSumPagedRep = new SumPagedRep<>();
        if (query.getAll()) {
            Set<String> keys = redisTemplate.keys(REVOKE_TOKEN_PREFIX + "*");
            if (keys != null) {
                long offset = paging.getPageSize() * paging.getPageNumber();
                int count = 0;
                Set<String> outputKey = new HashSet<>();
                for (String str : keys) {
                    if (count >= offset) {
                        outputKey.add(str);
                    }
                    if (outputKey.size() == paging.getPageSize())
                        break;
                    count++;
                }
                for (String str : outputKey) {
                    String s = redisTemplate.opsForValue().get(str);
                    RevokeToken deserialize = DomainRegistry.customObjectSerializer().deserialize(s, RevokeToken.class);
                    revokeTokens.add(deserialize);
                }
                revokeTokenSumPagedRep.setTotalItemCount((long) keys.size());
                revokeTokenSumPagedRep.setData(revokeTokens);
            }
        } else {
            query.getRevokeTokenId().forEach(tokenId -> {
                String s = redisTemplate.opsForValue().get(REVOKE_TOKEN_PREFIX + tokenId);
                if (s != null) {
                    RevokeToken deserialize = DomainRegistry.customObjectSerializer().deserialize(s, RevokeToken.class);
                    revokeTokens.add(deserialize);
                    revokeTokenSumPagedRep.setData(revokeTokens);
                    revokeTokenSumPagedRep.setTotalItemCount(1L);
                }
            });
        }
        return revokeTokenSumPagedRep;
    }

    public void add(RevokeToken revokeToken) {
        redisTemplate.opsForValue().set(REVOKE_TOKEN_PREFIX + revokeToken.getRevokeTokenId().getDomainId(), DomainRegistry.customObjectSerializer().serialize(revokeToken));
    }
}
