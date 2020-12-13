package com.mt.identityaccess.domain.model.client;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldLongEqualClause;
import org.springframework.stereotype.Component;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

@Component
public class AppBizClientSelectQueryBuilder extends SelectQueryBuilder<BizClient> {
    {
        supportedWhereField.put("clientId", new SelectFieldLongEqualClause<>(COMMON_ENTITY_ID));
    }
}
