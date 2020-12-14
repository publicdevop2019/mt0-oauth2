package com.hw.application.deprecated;

import com.hw.application.command.AppCreateBizUserCommand;
import com.hw.application.command.AppForgetBizUserPasswordCommand;
import com.hw.application.command.AppResetBizUserPasswordCommand;
import com.hw.application.representation.AppBizUserCardRep;
import com.hw.application.representation.AppBizUserRep;
import com.hw.domain.model.RevokeTokenService;
import com.hw.domain.model.UserNotificationService;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.shared.sql.SumPagedRep;
import com.hw.domain.model.user.User;
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
