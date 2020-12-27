package com.mt.identityaccess.domain.model.user;

import com.mt.common.Auditable;
import com.mt.common.ServiceUtility;
import com.mt.common.rest.Aggregate;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.command.*;
import com.mt.identityaccess.application.deprecated.AppBizUserApplicationService;
import com.mt.identityaccess.application.deprecated.AppPendingUserApplicationService;
import com.mt.identityaccess.application.representation.AppBizUserCardRep;
import com.mt.identityaccess.application.representation.AppPendingUserCardRep;
import com.mt.identityaccess.domain.model.RevokeTokenService;
import com.mt.identityaccess.domain.model.UserNotificationService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * root has ROLE_ROOT, ROLE_ADMIN, ROLE_USER
 * admin has ROLE_ADMIN, ROLE_USER
 * user has ROLE_USER
 */
@Entity
@Table(name = "user_")
@Data
public class User extends Auditable implements Aggregate {
    public static final String ENTITY_EMAIL = "email";
    public static final String ENTITY_SUBSCRIPTION = "subscription";
    public static final String ENTITY_LOCKED = "locked";
    public static final String ENTITY_GRANTED_AUTHORITIES = "grantedAuthorities";
    public static final String ENTITY_PWD_RESET_TOKEN = "pwdResetToken";
    public static final String ENTITY_PWD = "password";
    private static final String ROLE_ROOT = "ROLE_ROOT";
    private static final String QUERY_EMAIL = "email:";
    @Id
    private Long id;
    @Column(nullable = false)
    @NotBlank
    @Email
    private String email;
    @Column(nullable = false)
    @NotEmpty
    private String password;
    @Column
    private boolean locked = false;
    private String pwdResetToken;
    @Column
    @NotNull
    @NotEmpty
    @Convert(converter = Role.ResourceOwnerAuthorityConverter.class)
    private Set<Role> grantedAuthorities;
    @Column
    private boolean subscription;
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public User() {
    }

    /**
     * create user, grantedAuthorities is overwritten to ROLE_USER
     * if id present it will used instead generated
     */
    private User(AppCreateBizUserCommand command, Long id) {
        this.id = id;
        this.email = command.getEmail();
        this.password = command.getPassword();
        this.locked = false;
        this.grantedAuthorities = Collections.singleton(Role.ROLE_USER);
        this.subscription = false;
    }

    public static User create(Long id, AppCreateBizUserCommand command, PasswordEncoder encoder, AppPendingUserApplicationService pendingResourceOwnerRepo, AppBizUserApplicationService service) {
        validateBeforeCreate(command, pendingResourceOwnerRepo, service);
        command.setPassword(encoder.encode(command.getPassword()));
        return new User(command, id);
    }

    private static void validateBeforeCreate(AppCreateBizUserCommand command, AppPendingUserApplicationService pendingUserApplicationService, AppBizUserApplicationService bizUserApplicationService) {
        if (!StringUtils.hasText(command.getEmail()))
            throw new IllegalArgumentException("email is empty");
        if (!StringUtils.hasText(command.getPassword()))
            throw new IllegalArgumentException("password is empty");
        if (!StringUtils.hasText(command.getActivationCode()))
            throw new IllegalArgumentException("activationCode is empty");

        SumPagedRep<AppBizUserCardRep> appBizUserCardRepSumPagedRep = bizUserApplicationService.readByQuery(QUERY_EMAIL + command.getEmail(), null, null);
        if (!appBizUserCardRepSumPagedRep.getData().isEmpty())
            throw new IllegalArgumentException("already an user " + command.getEmail());
        SumPagedRep<AppPendingUserCardRep> data = pendingUserApplicationService.readByQuery(QUERY_EMAIL + command.getEmail(), null, null);
        if (data.getData().isEmpty())
            throw new IllegalArgumentException("please get activation code first");
        if (data.getData().get(0).getActivationCode() == null || !data.getData().get(0).getActivationCode().equals(command.getActivationCode()))
            throw new IllegalArgumentException("activation code mismatch");
    }

    public static void createForgetPwdToken(AppForgetBizUserPasswordCommand command,
                                            AppBizUserApplicationService appBizUserApplicationService, String changeId) {
        SumPagedRep<AppBizUserCardRep> byQuery = appBizUserApplicationService.readByQuery(QUERY_EMAIL + command.getEmail(), null, null);
        if (byQuery.getData().isEmpty())
            throw new IllegalArgumentException("user does not exist");
        Long id = byQuery.getData().get(0).getId();
        appBizUserApplicationService.replaceById(id, command, changeId);
    }

    private static String generateToken() {
        return "123456789";
//        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void resetPwd(AppResetBizUserPasswordCommand command, AppBizUserApplicationService appBizUserApplicationService, String changeId) {

        SumPagedRep<AppBizUserCardRep> var0 = appBizUserApplicationService.readByQuery(QUERY_EMAIL + command.getEmail(), null, null);
        if (var0.getData().isEmpty())
            throw new IllegalArgumentException("user does not exist");
        AppBizUserCardRep oneByEmail = var0.getData().get(0);
        appBizUserApplicationService.replaceById(oneByEmail.getId(), command, changeId);
    }

    public void validateBeforeDelete() {
        if (getGrantedAuthorities().stream().anyMatch(e -> ROLE_ROOT.equals(e.name())))
            throw new IllegalArgumentException("root account can not be modified");
    }

    /**
     * update pwd, id is part of bearer token,
     * must revoke issued token if pwd changed
     */
    public User replace(UserUpdateBizUserPasswordCommand command, RevokeTokenService tokenRevocationService, BCryptPasswordEncoder encoder) {
        if (!StringUtils.hasText(command.getPassword()) || !StringUtils.hasText(command.getCurrentPwd()))
            throw new IllegalArgumentException("password(s)");
        if (!encoder.matches(command.getCurrentPwd(), this.getPassword()))
            throw new IllegalArgumentException("wrong password");
//        tokenRevocationService.revokeUserToken(this.getId());
        this.setPassword(encoder.encode(command.getPassword()));
        return this;
    }


    public User replace(AdminUpdateBizUserCommand command, RevokeTokenService tokenRevocationService) {
        validateBeforeUpdate(command);
        shouldRevoke(command, tokenRevocationService);
        this.setGrantedAuthorities(command.getGrantedAuthorities());
        this.setLocked(command.isLocked());
        this.setSubscription(command.isSubscription());
        validateAfterUpdate();
        return this;
    }

    public void validateAfterUpdate() {
        if (isSubscription() && getGrantedAuthorities().stream().noneMatch(e -> "ROLE_ADMIN".equals(e.name()))) {
            throw new IllegalArgumentException("only admin can subscribe to new order");
        }
    }

    public void validateBeforeUpdate(AdminUpdateBizUserCommand command) {
        if (getGrantedAuthorities().stream().anyMatch(e -> ROLE_ROOT.equals(e.name())))
            throw new IllegalArgumentException("root account can not be modified");
        List<String> currentAuthorities = ServiceUtility.getAuthorities(command.getAuthorization());
        if (command.getGrantedAuthorities().stream().anyMatch(e -> ROLE_ROOT.equals(e.name())))
            throw new IllegalArgumentException("assign root grantedAuthorities is prohibited");
        if (currentAuthorities.stream().noneMatch(ROLE_ROOT::equals) && !getGrantedAuthorities().equals(command.getGrantedAuthorities()))
            throw new IllegalArgumentException("only root user can change grantedAuthorities");
        if (currentAuthorities.stream().noneMatch(ROLE_ROOT::equals) && isSubscription() != command.isSubscription())
            throw new IllegalArgumentException("only root user can change subscription");
    }

    /**
     * aspects: authority, lock
     * unlock a user should not revoke
     *
     * @return
     */
    public void shouldRevoke(AdminUpdateBizUserCommand command, RevokeTokenService tokenRevocationService) {
        if (authorityChanged(getGrantedAuthorities(), command.getGrantedAuthorities())) {
//            tokenRevocationService.revokeClientToken(this.getId());
        } else {
            if (Boolean.TRUE.equals(command.isLocked())){
//                tokenRevocationService.revokeClientToken(this.getId());
            }
        }
    }
    private boolean authorityChanged(Set<Role> old, Set<Role> next) {
        return !old.equals(next);
    }

    public void replace(Object command, UserNotificationService emailService, RevokeTokenService tokenRevocationService, BCryptPasswordEncoder encoder) {
        if (command instanceof AppForgetBizUserPasswordCommand) {
            replace((AppForgetBizUserPasswordCommand) command, emailService);
        } else if (command instanceof AppResetBizUserPasswordCommand) {
            replace((AppResetBizUserPasswordCommand) command, tokenRevocationService, encoder);
        }
    }

    private void replace(AppForgetBizUserPasswordCommand command, UserNotificationService emailService) {
        String s = generateToken();
        this.setPwdResetToken(s);
//        emailService.sendPasswordResetCodeTo(s, command.getEmail());
    }

    private void replace(AppResetBizUserPasswordCommand command, RevokeTokenService tokenRevocationService, BCryptPasswordEncoder encoder) {
        if (this.getPwdResetToken() == null)
            throw new IllegalArgumentException("token not exist");
        if (!this.getPwdResetToken().equals(command.getToken()))
            throw new IllegalArgumentException("token mismatch");
        this.setPassword(encoder.encode(command.getNewPassword()));
        this.setPwdResetToken(null);
//        tokenRevocationService.blacklist(this.getId());
    }
}
