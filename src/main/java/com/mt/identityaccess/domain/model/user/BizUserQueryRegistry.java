package com.mt.identityaccess.domain.model.user;

import com.mt.common.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizUserQueryRegistry extends RestfulQueryRegistry<User> {

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }
}
