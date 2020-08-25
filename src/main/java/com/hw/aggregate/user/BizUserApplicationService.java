package com.hw.aggregate.user;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.user.command.BizBizUserUpdatePwd;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import com.hw.aggregate.forget_pwd_req.model.ForgetPasswordRequest;
import com.hw.aggregate.user.model.PendingBizUser;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.authorize_code.model.TokenRevocationService;
import com.hw.aggregate.forget_pwd_req.ForgetPasswordRequestRepo;
import com.hw.aggregate.forget_pwd_req.EmailServiceImpl;
import com.hw.shared.BadRequestException;
import com.hw.shared.IdGenerator;
import com.hw.shared.ServiceUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * root has ROLE_ROOT, ROLE_ADMIN, ROLE_USER
 * admin has ROLE_ADMIN, ROLE_USER
 * user has ROLE_USER
 */
@Service
@Slf4j
public class BizUserApplicationService {
    @Autowired
    BizUserRepo resourceOwnerRepo;

    @Autowired
    PendingBizUserRepo pendingResourceOwnerRepo;

    @Autowired
    ForgetPasswordRequestRepo forgetPasswordRequestRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    TokenRevocationService<BizUser> tokenRevocationService;

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * update pwd, id is part of bearer token,
     * must revoke issued token if pwd changed
     */
    public void updateResourceOwnerPwd(BizBizUserUpdatePwd resourceOwner, String authorization) throws BadRequestException {
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

    public List<BizUser> readAllResourceOwners() {
        return resourceOwnerRepo.findAll();
    }

    /**
     * create user, grantedAuthorities is overwritten to ROLE_USER
     * if id present it will used instead generated
     */
    public BizUser createResourceOwner(PendingBizUser pendingResourceOwner) {
        return pendingResourceOwner.convert(encoder, pendingResourceOwnerRepo, resourceOwnerRepo, idGenerator);
    }

    public void createPendingResourceOwner(PendingBizUser pendingResourceOwner) {
        PendingBizUser pendingResourceOwner1 = PendingBizUser.create(pendingResourceOwner.getEmail(), pendingResourceOwnerRepo, resourceOwnerRepo, idGenerator);
        emailService.sendActivationCode(pendingResourceOwner1.getActivationCode(), pendingResourceOwner1.getEmail());
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

    /**
     * update grantedAuthorities, root user access can never be given, admin can only lock or unlock user
     */
    public void updateResourceOwner(BizUser updatedRO, Long id, String authorization) {
        preventRootAccountChange(id);
        List<String> currentAuthorities = ServiceUtility.getAuthority(authorization);
        if (updatedRO.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("assign root grantedAuthorities is prohibited");
        if (currentAuthorities.stream().noneMatch("ROLE_ROOT"::equals) && updatedRO.getAuthorities() != null)
            throw new BadRequestException("only root user can change grantedAuthorities");
        if (currentAuthorities.stream().noneMatch("ROLE_ROOT"::equals) && updatedRO.isSubscription())
            throw new BadRequestException("only root user can change subscription");
        BizUser storedRO = getResourceOwnerById(id);
        boolean b = tokenRevocationService.shouldRevoke(storedRO, updatedRO);
        if (updatedRO.getAuthorities() != null)
            storedRO.setGrantedAuthorities(new ArrayList<>((Collection<? extends GrantedAuthorityImpl<BizUserAuthorityEnum>>) updatedRO.getAuthorities()));
        if (updatedRO.getLocked() != null)
            storedRO.setLocked(updatedRO.getLocked());
        if (updatedRO.isSubscription()) {
            if (storedRO.getAuthorities().stream().anyMatch(e -> "ROLE_ADMIN".equals(e.getAuthority()))) {
                storedRO.setSubscription(Boolean.TRUE);
            } else {
                throw new BadRequestException("only admin or root can subscribe to new order");
            }
        }
        resourceOwnerRepo.save(storedRO);
        tokenRevocationService.blacklist(storedRO.getId().toString(), b);
    }

    public void deleteResourceOwner(Long id) {
        preventRootAccountChange(id);
        BizUser resourceOwnerById = getResourceOwnerById(id);
        resourceOwnerRepo.delete(resourceOwnerById);
        tokenRevocationService.blacklist(resourceOwnerById.getId().toString(), true);
    }


    public String getEmailSubscriber() {
        List<String> collect1 = resourceOwnerRepo.findAll().stream()
                .filter(BizUser::isSubscription)
                .map(BizUser::getEmail).collect(Collectors.toList());
        return String.join(",", collect1);
    }

    private void preventRootAccountChange(Long id) {
        Optional<BizUser> byId = resourceOwnerRepo.findById(id);
        if (byId.isPresent() && byId.get().getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("root account can not be modified");
    }

    private BizUser getResourceOwnerById(Long id) {
        Optional<BizUser> byId = resourceOwnerRepo.findById(id);
        if (byId.isEmpty())
            throw new BadRequestException("user not exist : " + id);
        return byId.get();
    }
}
