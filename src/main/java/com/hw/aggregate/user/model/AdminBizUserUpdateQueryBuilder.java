package com.hw.aggregate.user.model;

import com.hw.shared.rest.exception.UpdateFiledValueException;
import com.hw.shared.sql.builder.UpdateByIdQueryBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import static com.hw.aggregate.user.model.BizUser.ENTITY_LOCKED;

@Component
public class AdminBizUserUpdateQueryBuilder extends UpdateByIdQueryBuilder<BizUser> {
    {
        filedMap.put(ENTITY_LOCKED, ENTITY_LOCKED);
        filedTypeMap.put(ENTITY_LOCKED, this::parseBoolean);
    }

    private Boolean parseBoolean(@Nullable Object input) {
        if (input == null)
            throw new UpdateFiledValueException();
        if (input.getClass().equals(Boolean.class))
            return ((Boolean) input);
        return Boolean.parseBoolean((String) input);
    }

}
