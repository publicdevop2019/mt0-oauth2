package com.mt.identityaccess.domain.model.user;

import com.mt.common.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AdminBizUserDeleteQueryBuilder extends SoftDeleteQueryBuilder<User> {
}
