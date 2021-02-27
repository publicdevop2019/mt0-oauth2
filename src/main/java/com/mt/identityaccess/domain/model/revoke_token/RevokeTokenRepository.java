package com.mt.identityaccess.domain.model.revoke_token;

import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.SumPagedRep;

public interface RevokeTokenRepository {
    SumPagedRep<RevokeToken> revokeTokensOfQuery(RevokeTokenQuery revokeTokenQuery, PageConfig revokeTokenPaging, QueryConfig queryConfig);

    void add(RevokeToken revokeToken);
}
