package com.mt.access.domain.model.revoke_token;

import com.mt.common.domain.model.restful.SumPagedRep;

public interface RevokeTokenRepository {
    SumPagedRep<RevokeToken> revokeTokensOfQuery(RevokeTokenQuery revokeTokenQuery);

    void add(RevokeToken revokeToken);
}
