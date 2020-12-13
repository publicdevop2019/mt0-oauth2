package com.mt.identityaccess.domain.model.user;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AdminBizUserDeleteQueryBuilder extends SoftDeleteQueryBuilder<User> {
}
