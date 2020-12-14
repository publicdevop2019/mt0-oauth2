package com.mt.identityaccess.domain.model.client.builder;

import com.hw.shared.sql.builder.SoftDeleteQueryBuilder;
import com.mt.identityaccess.domain.model.client.Client;
import org.springframework.stereotype.Component;

@Component
public class RootBizClientDeleteQueryBuilder extends SoftDeleteQueryBuilder<Client> {
}
