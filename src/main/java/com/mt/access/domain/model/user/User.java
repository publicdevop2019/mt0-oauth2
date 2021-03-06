package com.mt.access.domain.model.user;

import com.google.common.base.Objects;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.event.UserAuthorityChanged;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.access.domain.model.user.event.UserUpdated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

/**
 * root has ROLE_ROOT, ROLE_ADMIN, ROLE_USER
 * admin has ROLE_ADMIN, ROLE_USER
 * user has ROLE_USER
 */
@Entity
@Table(name = "user_")
@NoArgsConstructor
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends Auditable {
    public static final String ENTITY_EMAIL = "email";
    public static final String ENTITY_SUBSCRIPTION = "subscription";
    public static final String ENTITY_LOCKED = "locked";
    public static final String ENTITY_GRANTED_AUTHORITIES = "grantedAuthorities";
    public static final String ENTITY_PWD_RESET_TOKEN = "pwdResetToken";
    public static final String ENTITY_PWD = "password";
    private static final String ROLE_ROOT = "ROLE_ROOT";
    private static final String QUERY_EMAIL = "email:";
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;
    @Setter(AccessLevel.PRIVATE)
    @Embedded
    @Getter
    private UserEmail email;
    @Embedded
    @Getter
    @Setter
    private UserPassword password;

    @Embedded
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;
    @Column
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private boolean locked = false;

    @Getter
    @Embedded
    private PasswordResetCode pwdResetToken;
    @Column
    @Convert(converter = Role.ResourceOwnerAuthorityConverter.class)
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Set<Role> grantedAuthorities;
    @Column
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private boolean subscription;

    public User(UserEmail userEmail, UserPassword password, UserId userId) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setEmail(userEmail);
        setPassword(password);
        setUserId(userId);
        setLocked(false);
        setGrantedAuthorities(Collections.singleton(Role.ROLE_USER));
        setSubscription(false);
        DomainRegistry.getUserValidationService().validate(this, new HttpValidationNotificationHandler());
    }

    public void setPwdResetToken(PasswordResetCode pwdResetToken) {
        this.pwdResetToken = pwdResetToken;
        DomainEventPublisher.instance().publish(new UserPwdResetCodeUpdated(getUserId(), getEmail(), getPwdResetToken()));
    }

    public boolean isNonRoot() {
        return getGrantedAuthorities().stream().noneMatch(e -> ROLE_ROOT.equals(e.name()));
    }

    public void replace(Set<Role> grantedAuthorities, boolean locked, boolean subscription) {
        if (getGrantedAuthorities().stream().anyMatch(e -> ROLE_ROOT.equals(e.name())))
            throw new IllegalArgumentException("root account can not be modified");
        if (grantedAuthorities.stream().anyMatch(e -> ROLE_ROOT.equals(e.name())))
            throw new IllegalArgumentException("assign root grantedAuthorities is prohibited");
        if (!getGrantedAuthorities().equals(grantedAuthorities) && !DomainRegistry.getAuthenticationService().userInRole(Role.ROLE_ROOT))
            throw new IllegalArgumentException("only root user can change grantedAuthorities");
        if (isSubscription() != subscription && !DomainRegistry.getAuthenticationService().userInRole(Role.ROLE_ROOT))
            throw new IllegalArgumentException("only root user can change subscription");
        if (!getGrantedAuthorities().equals(grantedAuthorities)) {
            DomainEventPublisher.instance().publish(new UserAuthorityChanged(getUserId()));
        }
        if (Boolean.TRUE.equals(locked)) {
            DomainEventPublisher.instance().publish(new UserGetLocked(getUserId()));
        }
        setGrantedAuthorities(grantedAuthorities);
        setLocked(locked);
        setSubscription(subscription);
        if (isSubscription() && getGrantedAuthorities().stream().noneMatch(e -> "ROLE_ADMIN".equals(e.name()))) {
            throw new IllegalArgumentException("only admin can subscribe to new order");
        }
    }

    @PreUpdate
    private void preUpdate() {
        DomainEventPublisher.instance().publish(new UserUpdated(getUserId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equal(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), userId);
    }
}
