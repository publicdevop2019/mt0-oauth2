package com.hw.aggregate.client.model;

import com.hw.shared.sql.builder.HardDeleteQueryBuilder;
import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class RootBizClientDeleteQueryBuilder extends SoftDeleteQueryBuilder<BizClient> {
}
