package com.hw.aggregate.pending_user.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class PendingUserQueryRegistry extends RestfulQueryRegistry<PendingUser> {
    @Override
    public Class<PendingUser> getEntityClass() {
        return PendingUser.class;
    }
}
