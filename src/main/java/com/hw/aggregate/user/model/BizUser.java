package com.hw.aggregate.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.pending_user.representation.AppPendingUserCardRep;
import com.hw.aggregate.user.AppBizUserApplicationService;
import com.hw.aggregate.user.RevokeBizUserTokenService;
import com.hw.aggregate.user.command.CreateBizUserCommand;
import com.hw.aggregate.user.command.UpdateBizUserCommand;
import com.hw.aggregate.user.representation.AppBizUserCardRep;
import com.hw.shared.Auditable;
import com.hw.shared.BadRequestException;
import com.hw.shared.ServiceUtility;
import com.hw.shared.rest.IdBasedEntity;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Id
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Email
    private String email;
    public static final String ENTITY_EMAIL = "email";

    @Column(nullable = false)
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    @NotNull
    private Boolean locked;

    @Column(nullable = false)
    @NotNull
    @NotEmpty
    @Convert(converter = BizUserAuthorityEnum.ResourceOwnerAuthorityConverter.class)
    private List<@Valid @NotNull GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities;

    @Column
    private boolean subscription;
    public static final String ENTITY_SUBSCRIPTION = "subscription";

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

    public static BizUser create(Long id, CreateBizUserCommand command, PasswordEncoder encoder, AppPendingUserApplicationService pendingResourceOwnerRepo, AppBizUserApplicationService service) {
        validate(command, pendingResourceOwnerRepo, service);
        command.setPassword(encoder.encode(command.getPassword()));
        return new BizUser(command, id);
    }

    /**
     * create user, grantedAuthorities is overwritten to ROLE_USER
     * if id present it will used instead generated
     */
    private BizUser(CreateBizUserCommand command, Long id) {
        this.id = id;
        this.email = command.getEmail();
        this.password = command.getPassword();
        this.locked = false;
        this.grantedAuthorities = Collections.singletonList(new GrantedAuthorityImpl(BizUserAuthorityEnum.ROLE_USER));
        this.subscription = false;
    }

    private static void validate(CreateBizUserCommand command, AppPendingUserApplicationService pendingUserApplicationService, AppBizUserApplicationService bizUserApplicationService) throws BadRequestException {
        if (!StringUtils.hasText(command.getEmail()))
            throw new BadRequestException("email is empty");
        if (!StringUtils.hasText(command.getPassword()))
            throw new BadRequestException("password is empty");
        if (!StringUtils.hasText(command.getActivationCode()))
            throw new BadRequestException("activationCode is empty");

        SumPagedRep<AppBizUserCardRep> appBizUserCardRepSumPagedRep = bizUserApplicationService.readByQuery("email:" + command.getEmail(), null, null);
        if (appBizUserCardRepSumPagedRep.getData().size() != 0)
            throw new BadRequestException("already an user " + command.getEmail());
        SumPagedRep<AppPendingUserCardRep> appPendingUserCardRepSumPagedRep = pendingUserApplicationService.readByQuery("email:" + command.getEmail(), null, null);
        if (appPendingUserCardRepSumPagedRep.getData().size() == 0)
            throw new BadRequestException("please get activation code first");
        if (!appPendingUserCardRepSumPagedRep.getData().get(0).getActivationCode().equals(command.getActivationCode()))
            throw new BadRequestException("activation code mismatch");
    }

    public BizUser replace(UpdateBizUserCommand command, RevokeBizUserTokenService tokenRevocationService) {
        preventRootAccountChange(this);
        List<String> currentAuthorities = ServiceUtility.getAuthority(command.getAuthorization());
        if (this.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("assign root grantedAuthorities is prohibited");
        if (currentAuthorities.stream().noneMatch("ROLE_ROOT"::equals) && this.getAuthorities() != null)
            throw new BadRequestException("only root user can change grantedAuthorities");
        if (currentAuthorities.stream().noneMatch("ROLE_ROOT"::equals) && this.isSubscription())
            throw new BadRequestException("only root user can change subscription");
        boolean b = shouldRevoke(this, command);
        tokenRevocationService.blacklist(this.getId().toString(), b);

        this.setGrantedAuthorities(command.getGrantedAuthorities());
        this.setLocked(command.getLocked());
        if (command.isSubscription()) {
            if (this.getAuthorities().stream().anyMatch(e -> "ROLE_ADMIN".equals(e.getAuthority()))) {
                this.setSubscription(Boolean.TRUE);
            } else {
                throw new BadRequestException("only admin or root can subscribe to new order");
            }
        }
        return this;
    }

    private void preventRootAccountChange(BizUser bizUser) {
        if (bizUser.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("root account can not be modified");
    }

    /**
     * aspects: authority, lock
     * unlock a user should not revoke
     *
     * @param oldResourceOwner
     * @param newResourceOwner
     * @return
     */
    public boolean shouldRevoke(BizUser oldResourceOwner, UpdateBizUserCommand newResourceOwner) {
        if (authorityChanged(oldResourceOwner, newResourceOwner)) {
            return true;
        } else return lockUser(newResourceOwner);
    }

    private boolean lockUser(UpdateBizUserCommand newResourceOwner) {
        return Boolean.TRUE.equals(newResourceOwner.getLocked());
    }

    private boolean authorityChanged(BizUser oldResourceOwner, UpdateBizUserCommand newResourceOwner) {
        HashSet<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities = new HashSet<>(oldResourceOwner.getGrantedAuthorities());
        HashSet<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities1 = new HashSet<>(newResourceOwner.getGrantedAuthorities());
        return !grantedAuthorities.equals(grantedAuthorities1);
    }
}
