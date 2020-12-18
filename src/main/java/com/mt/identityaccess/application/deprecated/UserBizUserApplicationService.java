package com.mt.identityaccess.application.deprecated;

import com.mt.common.rest.RoleBasedRestfulService;
import com.mt.common.rest.VoidTypedClass;
import com.mt.common.sql.RestfulQueryRegistry;
import com.mt.identityaccess.domain.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserBizUserApplicationService extends RoleBasedRestfulService<User, Void, Void, VoidTypedClass> {
    {
        entityClass = User.class;
        role = RestfulQueryRegistry.RoleEnum.USER;
    }

    @Autowired
    BCryptPasswordEncoder encoder;

//    @Autowired
//    HttpRevokeBizUserTokenAdapter tokenRevocationService;

//    @Override
//    public User replaceEntity(User storedBizUser, Object command) {
//        return storedBizUser.replace((UserUpdateBizUserPasswordCommand) command, tokenRevocationService, encoder);
//    }


}
