package com.mt.identityaccess.domain.model.user;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizUserQueryRegistry extends RestfulQueryRegistry<BizUser> {

    @Override
    public Class<BizUser> getEntityClass() {
        return BizUser.class;
    }
}
