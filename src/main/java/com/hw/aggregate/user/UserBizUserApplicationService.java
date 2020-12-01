package com.hw.aggregate.user;

import com.hw.aggregate.user.command.UserUpdateBizUserPasswordCommand;
import com.hw.aggregate.user.model.BizUser;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UserBizUserApplicationService extends RoleBasedRestfulService<BizUser, Void, Void, VoidTypedClass> {
    {
        entityClass = BizUser.class;
        role = RestfulQueryRegistry.RoleEnum.USER;
    }

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    RevokeBizUserTokenService tokenRevocationService;

    @Override
    public BizUser replaceEntity(BizUser storedBizUser, Object command) {
        return storedBizUser.replace((UserUpdateBizUserPasswordCommand) command, tokenRevocationService, encoder);
    }


}
