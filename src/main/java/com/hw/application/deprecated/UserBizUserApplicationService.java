package com.hw.application.deprecated;

import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.domain.model.user.User;
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
