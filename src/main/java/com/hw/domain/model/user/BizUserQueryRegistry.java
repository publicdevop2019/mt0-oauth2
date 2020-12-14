package com.hw.domain.model.user;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizUserQueryRegistry extends RestfulQueryRegistry<User> {

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }
}
