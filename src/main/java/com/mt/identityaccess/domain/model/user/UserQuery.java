package com.mt.identityaccess.domain.model.user;

import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.identityaccess.domain.model.user.UserId;

public class UserQuery extends QueryCriteria {

    public UserQuery(String queryParam) {
        super(queryParam);
    }

    public UserQuery(UserId userId) {
        super(userId);
    }
}
