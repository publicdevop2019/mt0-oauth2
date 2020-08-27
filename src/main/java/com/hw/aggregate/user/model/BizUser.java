package com.hw.aggregate.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.pending_user.representation.AppPendingUserCardRep;
import com.hw.aggregate.user.AppBizUserApplicationService;
import com.hw.aggregate.user.BizUserRepo;
import com.hw.aggregate.user.PwdResetEmailService;
import com.hw.aggregate.user.RevokeBizUserTokenService;
import com.hw.aggregate.user.command.*;
import com.hw.aggregate.user.representation.AdminBizUserRep;
import com.hw.aggregate.user.representation.AppBizUserCardRep;
import com.hw.shared.Auditable;
import com.hw.shared.ServiceUtility;
import com.hw.shared.rest.IdBasedEntity;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * root has ROLE_ROOT, ROLE_ADMIN, ROLE_USER
 * admin has ROLE_ADMIN, ROLE_USER
 * user has ROLE_USER
 */
@Entity
@Table
@Data
public class BizUser extends Auditable implements UserDetails, IdBasedEntity {
    public static final String ENTITY_EMAIL = "email";
    public static final String ENTITY_SUBSCRIPTION = "subscription";
    @Id
    private Long id;
    @Column(nullable = false)
    @NotBlank
    @Email
    private String email;
    @Column(nullable = false)
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(nullable = false)
    @NotNull
    private Boolean locked;
    private String pwdResetToken;
    @Column(nullable = false)
    @NotNull
    @NotEmpty
    @Convert(converter = BizUserAuthorityEnum.ResourceOwnerAuthorityConverter.class)
    private List<@Valid @NotNull GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities;
    @Column
    private boolean subscription;

    public BizUser() {
    }

    /**
     * create user, grantedAuthorities is overwritten to ROLE_USER
     * if id present it will used instead generated
     */
    private BizUser(PublicCreateBizUserCommand command, Long id) {
        this.id = id;
        this.email = command.getEmail();
        this.password = command.getPassword();
        this.locked = false;
        this.grantedAuthorities = Collections.singletonList(new GrantedAuthorityImpl(BizUserAuthorityEnum.ROLE_USER));
        this.subscription = false;
    }

    public static void canBeDeleted(AdminBizUserRep adminBizUserRep, RevokeBizUserTokenService tokenRevocationService) {
        if (adminBizUserRep.getGrantedAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new IllegalArgumentException("root account can not be modified");
        tokenRevocationService.blacklist(adminBizUserRep.getId());
    }

    public static BizUser create(Long id, PublicCreateBizUserCommand command, PasswordEncoder encoder, AppPendingUserApplicationService pendingResourceOwnerRepo, AppBizUserApplicationService service) {
        validate(command, pendingResourceOwnerRepo, service);
        command.setPassword(encoder.encode(command.getPassword()));
        return new BizUser(command, id);
    }

    private static void validate(PublicCreateBizUserCommand command, AppPendingUserApplicationService pendingUserApplicationService, AppBizUserApplicationService bizUserApplicationService) throws IllegalArgumentException {
        if (!StringUtils.hasText(command.getEmail()))
            throw new IllegalArgumentException("email is empty");
        if (!StringUtils.hasText(command.getPassword()))
            throw new IllegalArgumentException("password is empty");
        if (!StringUtils.hasText(command.getActivationCode()))
            throw new IllegalArgumentException("activationCode is empty");

        SumPagedRep<AppBizUserCardRep> appBizUserCardRepSumPagedRep = bizUserApplicationService.readByQuery("email:" + command.getEmail(), null, null);
        if (appBizUserCardRepSumPagedRep.getData().size() != 0)
            throw new IllegalArgumentException("already an user " + command.getEmail());
        SumPagedRep<AppPendingUserCardRep> appPendingUserCardRepSumPagedRep = pendingUserApplicationService.readByQuery("email:" + command.getEmail(), null, null);
        if (appPendingUserCardRepSumPagedRep.getData().size() == 0)
            throw new IllegalArgumentException("please get activation code first");
        if (!appPendingUserCardRepSumPagedRep.getData().get(0).getActivationCode().equals(command.getActivationCode()))
            throw new IllegalArgumentException("activation code mismatch");
    }

    public static BizUser createForgetPwdToken(PublicForgetPasswordCommand command, BizUserRepo resourceOwnerRepo, PwdResetEmailService emailService) {
        if (!StringUtils.hasText(command.getEmail()))
            throw new IllegalArgumentException("empty email");
        BizUser bizUser = resourceOwnerRepo.findOneByEmail(command.getEmail());
        if (bizUser == null)
            throw new IllegalArgumentException("user does not exist");
        bizUser.setPwdResetToken(generateToken());
        BizUser save = resourceOwnerRepo.save(bizUser);
        emailService.sendPasswordResetLink(save.getPwdResetToken(), save.getEmail());
        return bizUser;
    }

    private static String generateToken() {
        return "123456789";
//        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void resetPwd(PublicResetPwdCommand command, BizUserRepo resourceOwnerRepo, RevokeBizUserTokenService service, BCryptPasswordEncoder encoder) {
        if (!StringUtils.hasText(command.getEmail()))
            throw new IllegalArgumentException("empty email");
        if (!StringUtils.hasText(command.getToken()))
            throw new IllegalArgumentException("empty token");
        if (!StringUtils.hasText(command.getNewPassword()))
            throw new IllegalArgumentException("empty new password");
        BizUser oneByEmail = resourceOwnerRepo.findOneByEmail(command.getEmail());
        if (oneByEmail == null)
            throw new IllegalArgumentException("not an user");
        BizUser oneByEmail2 = resourceOwnerRepo.findOneByEmail(command.getEmail());
        if (oneByEmail2 == null)
            throw new IllegalArgumentException("user does not exist");
        if (oneByEmail.getPwdResetToken() == null)
            throw new IllegalArgumentException("token not exist");
        if (!oneByEmail.getPwdResetToken().equals(command.getToken()))
            throw new IllegalArgumentException("token mismatch");
        // reset password
        oneByEmail.setPassword(encoder.encode(command.getNewPassword()));
        resourceOwnerRepo.save(oneByEmail);
        oneByEmail.setPwdResetToken(null);
        service.blacklist(oneByEmail.getId());
    }

    /**
     * update pwd, id is part of bearer token,
     * must revoke issued token if pwd changed
     */
    public BizUser replace(UserUpdateBizUserCommand command, RevokeBizUserTokenService tokenRevocationService, BCryptPasswordEncoder encoder) {
        if (!StringUtils.hasText(command.getPassword()) || !StringUtils.hasText(command.getCurrentPwd()))
            throw new IllegalArgumentException("password(s)");
        if (!encoder.matches(command.getCurrentPwd(), this.getPassword()))
            throw new IllegalArgumentException("wrong password");
        tokenRevocationService.blacklist(this.getId());
        this.setPassword(encoder.encode(command.getPassword()));
        return this;
    }

    /**
     * make sure grantedAuthorities only get serialized once
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked == null || !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public BizUser replace(AdminUpdateBizUserCommand command, RevokeBizUserTokenService tokenRevocationService) {
        preventRootAccountChange(this);
        List<String> currentAuthorities = ServiceUtility.getAuthority(command.getAuthorization());
        if (this.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new IllegalArgumentException("assign root grantedAuthorities is prohibited");
        if (currentAuthorities.stream().noneMatch("ROLE_ROOT"::equals) && this.getAuthorities() != null)
            throw new IllegalArgumentException("only root user can change grantedAuthorities");
        if (currentAuthorities.stream().noneMatch("ROLE_ROOT"::equals) && this.isSubscription())
            throw new IllegalArgumentException("only root user can change subscription");
        boolean b = shouldRevoke(this, command);
        if (b)
            tokenRevocationService.blacklist(this.getId());
        this.setGrantedAuthorities(command.getGrantedAuthorities());
        this.setLocked(command.getLocked());
        if (command.isSubscription()) {
            if (this.getAuthorities().stream().anyMatch(e -> "ROLE_ADMIN".equals(e.getAuthority()))) {
                this.setSubscription(Boolean.TRUE);
            } else {
                throw new IllegalArgumentException("only admin or root can subscribe to new order");
            }
        }
        return this;
    }

    private void preventRootAccountChange(BizUser bizUser) {
        if (bizUser.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new IllegalArgumentException("root account can not be modified");
    }

    /**
     * aspects: authority, lock
     * unlock a user should not revoke
     *
     * @param oldResourceOwner
     * @param newResourceOwner
     * @return
     */
    public boolean shouldRevoke(BizUser oldResourceOwner, AdminUpdateBizUserCommand newResourceOwner) {
        if (authorityChanged(oldResourceOwner, newResourceOwner)) {
            return true;
        } else return lockUser(newResourceOwner);
    }

    private boolean lockUser(AdminUpdateBizUserCommand newResourceOwner) {
        return Boolean.TRUE.equals(newResourceOwner.getLocked());
    }

    private boolean authorityChanged(BizUser oldResourceOwner, AdminUpdateBizUserCommand newResourceOwner) {
        HashSet<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities = new HashSet<>(oldResourceOwner.getGrantedAuthorities());
        HashSet<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities1 = new HashSet<>(newResourceOwner.getGrantedAuthorities());
        return !grantedAuthorities.equals(grantedAuthorities1);
    }
}
