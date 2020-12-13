package com.mt.identityaccess.application;

import com.mt.identityaccess.application.command.UserUpdateBizUserPasswordCommand;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.mt.identityaccess.domain.model.user.BizUser;
import com.mt.identityaccess.port.adapter.service.HttpRevokeBizUserTokenAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    HttpRevokeBizUserTokenAdapter tokenRevocationService;

    @Override
    public BizUser replaceEntity(BizUser storedBizUser, Object command) {
        return storedBizUser.replace((UserUpdateBizUserPasswordCommand) command, tokenRevocationService, encoder);
    }


}
