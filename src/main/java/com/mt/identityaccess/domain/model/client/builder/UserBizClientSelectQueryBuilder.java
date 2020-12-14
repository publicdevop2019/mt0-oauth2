package com.mt.identityaccess.domain.model.client.builder;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldLongEqualClause;
import com.mt.identityaccess.domain.model.client.Client;
import org.springframework.stereotype.Component;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

@Component
public class UserBizClientSelectQueryBuilder extends SelectQueryBuilder<Client> {
    {
        supportedWhereField.put("clientId", new SelectFieldLongEqualClause<>(COMMON_ENTITY_ID));
    }
}
