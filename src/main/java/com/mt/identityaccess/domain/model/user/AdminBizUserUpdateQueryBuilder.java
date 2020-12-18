package com.mt.identityaccess.domain.model.user;

import com.mt.common.rest.exception.UpdateFiledValueException;
import com.mt.common.sql.builder.UpdateByIdQueryBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.mt.identityaccess.domain.model.user.User.ENTITY_LOCKED;

@Component
public class AdminBizUserUpdateQueryBuilder extends UpdateByIdQueryBuilder<User> {
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
