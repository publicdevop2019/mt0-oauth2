package com.mt.identityaccess.port.adapter.persistence.revoke_token;

import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.client.QueryConfig;
import com.mt.identityaccess.application.revoke_token.RevokeTokenPaging;
import com.mt.identityaccess.application.revoke_token.RevokeTokenQuery;
import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import com.mt.identityaccess.domain.model.revoke_token.RevokeTokenRepository;
import com.mt.identityaccess.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJpaRevokeTokenRepository extends JpaRepository<RevokeToken, Long>, RevokeTokenRepository {
    default SumPagedRep<RevokeToken> revokeTokensOfQuery(RevokeTokenQuery revokeTokenQuery, RevokeTokenPaging revokeTokenPaging, QueryConfig queryConfig) {
        return getSumPagedRep(revokeTokenQuery.getValue(), revokeTokenPaging.value(), queryConfig.value());
    }

    default void add(RevokeToken revokeToken) {
        save(revokeToken);
    }

    private SumPagedRep<RevokeToken> getSumPagedRep(String query, String page, String config) {
        RevokeTokenQueryBuilder userQueryBuilder = QueryBuilderRegistry.revokeTokenQueryBuilder();
        List<RevokeToken> select = userQueryBuilder.select(query, page, RevokeToken.class);
        Long aLong = null;
        if (!skipCount(config)) {
            aLong = userQueryBuilder.selectCount(query, RevokeToken.class);
        }
        return new SumPagedRep<>(select, aLong);
    }

    private boolean skipCount(String config) {
        return config != null && config.contains("sc:1");
    }
}
