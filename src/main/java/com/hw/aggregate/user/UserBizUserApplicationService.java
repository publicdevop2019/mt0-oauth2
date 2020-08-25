package com.hw.aggregate.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.pending_user.PendingUserRepo;
import com.hw.aggregate.user.command.UpdateBizUserCommand;
import com.hw.aggregate.user.command.UpdateBizUserPwdCommand;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserQueryRegistry;
import com.hw.shared.BadRequestException;
import com.hw.shared.IdGenerator;
import com.hw.shared.ServiceUtility;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Component
public class UserBizUserApplicationService extends DefaultRoleBasedRestfulService<BizUser, Void, Void, VoidTypedClass> {
    @Autowired
    BizUserRepo resourceOwnerRepo;

    @Autowired
    PendingUserRepo pendingResourceOwnerRepo;

    @Autowired
    ForgetPasswordRequestRepo forgetPasswordRequestRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    RevokeBizUserTokenService tokenRevocationService;

    @Autowired
    PwdResetEmailService emailService;
    @Autowired
    private BizUserQueryRegistry bizUserQueryRegistry;
    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private ChangeRepository changeRepository2;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AppPendingUserApplicationService pendingUserApplicationService;
    @Autowired
    private AppBizUserApplicationService bizUserApplicationService;


    @PostConstruct
    private void setUp() {
        repo = resourceOwnerRepo;
        queryRegistry = bizUserQueryRegistry;
        entityClass = BizUser.class;
        role = RestfulQueryRegistry.RoleEnum.USER;
        idGenerator = idGenerator2;
        changeRepository = changeRepository2;
        om = objectMapper;
    }

    /**
     * update pwd, id is part of bearer token,
     * must revoke issued token if pwd changed
     */
    public void updateResourceOwnerPwd(UpdateBizUserPwdCommand resourceOwner, String authorization) throws BadRequestException {
        String userId = ServiceUtility.getUserId(authorization);
        if (!StringUtils.hasText(resourceOwner.getPassword()) || !StringUtils.hasText(resourceOwner.getCurrentPwd()))
            throw new BadRequestException("password(s)");
        BizUser resourceOwnerById = getResourceOwnerById(Long.parseLong(userId));
        if (!encoder.matches(resourceOwner.getCurrentPwd(), resourceOwnerById.getPassword()))
            throw new BadRequestException("wrong password");
        resourceOwnerById.setPassword(encoder.encode(resourceOwner.getPassword()));
        resourceOwnerRepo.save(resourceOwnerById);
        tokenRevocationService.blacklist(resourceOwnerById.getId().toString(), true);
    }


    private BizUser getResourceOwnerById(Long id) {
        Optional<BizUser> byId = resourceOwnerRepo.findById(id);
        if (byId.isEmpty())
            throw new BadRequestException("user not exist : " + id);
        return byId.get();
    }

    @Override
    public BizUser replaceEntity(BizUser storedBizUser, Object command) {
        return null;
    }

    @Override
    public Void getEntitySumRepresentation(BizUser bizUser) {
        return null;
    }

    @Override
    public Void getEntityRepresentation(BizUser bizUser) {
        return null;
    }

    @Override
    protected BizUser createEntity(long id, Object command) {
        return null;
    }
}
