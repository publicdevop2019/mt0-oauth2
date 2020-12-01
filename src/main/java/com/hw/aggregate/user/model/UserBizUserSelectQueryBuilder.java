package com.hw.aggregate.user.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserBizUserSelectQueryBuilder extends SelectQueryBuilder<BizUser> {
    {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
    }
}
