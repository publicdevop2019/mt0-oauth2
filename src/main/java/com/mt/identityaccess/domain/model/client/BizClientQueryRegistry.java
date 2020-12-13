package com.mt.identityaccess.domain.model.client;

import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.stereotype.Component;

@Component
public class BizClientQueryRegistry extends RestfulQueryRegistry<Client> {
    @Override
    public Class<Client> getEntityClass() {
        return Client.class;
    }

    public BizClientQueryRegistry() {
        cacheable.put(RoleEnum.ROOT, false);
    }
}
