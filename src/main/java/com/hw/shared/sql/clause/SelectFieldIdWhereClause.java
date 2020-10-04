package com.hw.shared.sql.clause;

import static com.hw.shared.AppConstant.COMMON_ENTITY_ID;

public class SelectFieldIdWhereClause<T> extends SelectFieldLongEqualClause<T> {
    public SelectFieldIdWhereClause() {
        super(COMMON_ENTITY_ID);
    }
}
