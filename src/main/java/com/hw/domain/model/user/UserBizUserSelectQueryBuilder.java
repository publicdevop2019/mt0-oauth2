package com.hw.domain.model.user;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserBizUserSelectQueryBuilder extends SelectQueryBuilder<User> {
    {
        DEFAULT_PAGE_SIZE = 1;
        MAX_PAGE_SIZE = 1;
    }
}
