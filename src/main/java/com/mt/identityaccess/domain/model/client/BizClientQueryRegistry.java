package com.mt.identityaccess.domain.model.client;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizClientQueryRegistry extends RestfulQueryRegistry<BizClient> {
    @Override
    public Class<BizClient> getEntityClass() {
        return BizClient.class;
    }

    public BizClientQueryRegistry() {
        cacheable.put(RoleEnum.ROOT, false);
    }
}
