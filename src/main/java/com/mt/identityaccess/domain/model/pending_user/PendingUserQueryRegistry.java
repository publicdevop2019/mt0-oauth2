package com.mt.identityaccess.domain.model.pending_user;

import com.mt.common.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class PendingUserQueryRegistry extends RestfulQueryRegistry<PendingUser> {
    @Override
    public Class<PendingUser> getEntityClass() {
        return PendingUser.class;
    }
}
