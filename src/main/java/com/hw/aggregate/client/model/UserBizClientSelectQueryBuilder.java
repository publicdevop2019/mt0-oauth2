package com.hw.aggregate.client.model;

import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.hw.shared.sql.clause.SelectFieldLongEqualClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

@Component
public class UserBizClientSelectQueryBuilder extends SelectQueryBuilder<BizClient> {
    UserBizClientSelectQueryBuilder() {
        supportedWhereField.put("clientId", new SelectFieldLongEqualClause<>(COMMON_ENTITY_ID));
    }
}
