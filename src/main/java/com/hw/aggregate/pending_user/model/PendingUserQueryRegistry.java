package com.hw.aggregate.pending_user.model;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PendingUserQueryRegistry extends RestfulQueryRegistry<PendingUser> {
    @Override
    public Class<PendingUser> getEntityClass() {
        return PendingUser.class;
    }
    @PostConstruct
    private void setUp() {
        cacheable.put(RoleEnum.USER, true);
        cacheable.put(RoleEnum.ADMIN, true);
        cacheable.put(RoleEnum.APP, true);
        cacheable.put(RoleEnum.PUBLIC, true);
        cacheable.put(RoleEnum.ROOT, true);
    }
}
