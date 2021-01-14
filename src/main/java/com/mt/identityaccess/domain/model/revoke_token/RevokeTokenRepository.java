package com.mt.identityaccess.domain.model.revoke_token;

import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.revoke_token.RevokeTokenQuery;

public interface RevokeTokenRepository {
    SumPagedRep<RevokeToken> revokeTokensOfQuery(RevokeTokenQuery revokeTokenQuery, DefaultPaging revokeTokenPaging, QueryConfig queryConfig);

    void add(RevokeToken revokeToken);
}
