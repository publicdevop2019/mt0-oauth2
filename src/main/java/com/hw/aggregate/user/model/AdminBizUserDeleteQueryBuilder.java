package com.hw.aggregate.user.model;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class AdminBizUserDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizUser> {
}
