package com.mt.identityaccess.application.deprecated;

import com.mt.identityaccess.application.command.AppCreateBizUserCommand;
import com.mt.identityaccess.application.command.AppForgetBizUserPasswordCommand;
import com.mt.identityaccess.application.command.AppResetBizUserPasswordCommand;
import com.mt.identityaccess.application.representation.AppBizUserCardRep;
import com.mt.identityaccess.application.representation.AppBizUserRep;
import com.mt.identityaccess.domain.service.RevokeTokenService;
import com.mt.identityaccess.domain.service.UserNotificationService;
import com.mt.common.rest.RoleBasedRestfulService;
import com.mt.common.rest.VoidTypedClass;
import com.mt.common.sql.RestfulQueryRegistry;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.domain.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AppBizUserApplicationService extends RoleBasedRestfulService<User, AppBizUserCardRep, AppBizUserRep, VoidTypedClass> implements UserDetailsService {
    {
        entityClass = User.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RevokeTokenService tokenRevocationService;

    @Autowired
    private UserNotificationService emailService;

    @Autowired
    private AppPendingUserApplicationService pendingUserApplicationService;


    @Override
    public User replaceEntity(User bizUser, Object command) {
        bizUser.replace(command, emailService, tokenRevocationService, encoder);
        return bizUser;
    }

    @Override
    public AppBizUserCardRep getEntitySumRepresentation(User bizUser) {
        return new AppBizUserCardRep(bizUser);
    }

    @Override
    public AppBizUserRep getEntityRepresentation(User bizUser) {
        return new AppBizUserRep(bizUser);
    }

    public void sendForgetPassword(AppForgetBizUserPasswordCommand command, String changeId) {
        User.createForgetPwdToken(command, this, changeId);
    }

    public void resetPassword(AppResetBizUserPasswordCommand command, String changeId) {
        User.resetPwd(command, this, changeId);
    }

    @Override
    protected User createEntity(long id, Object command) {
        return User.create(id, (AppCreateBizUserCommand) command, encoder, pendingUserApplicationService, this);

    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrId) {
        try {
            return readById(Long.parseLong(usernameOrId));
        } catch (NumberFormatException ex) {
            SumPagedRep<AppBizUserCardRep> appBizUserCardRepSumPagedRep = readByQuery("email:" + usernameOrId, null, null);
            if (appBizUserCardRepSumPagedRep.getData().isEmpty())
                return null;
            return readById(appBizUserCardRepSumPagedRep.getData().get(0).getId());
        }
    }
}
