package com.hw.aggregate.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.pending_user.PendingUserRepo;
import com.hw.aggregate.user.command.CreateBizUserCommand;
import com.hw.aggregate.user.command.UpdateBizUserPwdCommand;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import com.hw.aggregate.user.model.BizUserQueryRegistry;
import com.hw.aggregate.user.model.ForgetPasswordRequest;
import com.hw.aggregate.user.representation.AdminBizUserCardRep;
import com.hw.aggregate.user.representation.AdminBizUserRep;
import com.hw.shared.BadRequestException;
import com.hw.shared.IdGenerator;
import com.hw.shared.ServiceUtility;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Slf4j
public class PublicBizUserApplicationService extends DefaultRoleBasedRestfulService<BizUser, Void, Void, VoidTypedClass> {
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
        role = RestfulQueryRegistry.RoleEnum.PUBLIC;
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




    public void sendForgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        ForgetPasswordRequest forgetPasswordRequest1 = ForgetPasswordRequest.create(forgetPasswordRequest.getEmail(), forgetPasswordRequestRepo, resourceOwnerRepo, idGenerator);
        emailService.sendPasswordResetLink(forgetPasswordRequest1.getToken(), forgetPasswordRequest.getEmail());
    }

    public void resetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        forgetPasswordRequest.verifyToken(forgetPasswordRequestRepo, resourceOwnerRepo);
        // reset password
        BizUser oneByEmail = resourceOwnerRepo.findOneByEmail(forgetPasswordRequest.getEmail());
        oneByEmail.setPassword(encoder.encode(forgetPasswordRequest.getNewPassword()));
        resourceOwnerRepo.save(oneByEmail);
        tokenRevocationService.blacklist(oneByEmail.getId().toString(), true);
    }




    private BizUser getResourceOwnerById(Long id) {
        Optional<BizUser> byId = resourceOwnerRepo.findById(id);
        if (byId.isEmpty())
            throw new BadRequestException("user not exist : " + id);
        return byId.get();
    }

    @Override
    public BizUser replaceEntity(BizUser bizUser, Object command) {
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
        return BizUser.create(id, (CreateBizUserCommand) command,encoder,pendingUserApplicationService,bizUserApplicationService);

    }
}
