package com.mt.identityaccess.domain.model.pending_user;

import com.google.common.base.Objects;
import com.mt.common.audit.Auditable;
import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PendingUser extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column
    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    private RegistrationEmail registrationEmail;

    @Column
    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    private ActivationCode activationCode;

    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public PendingUser(RegistrationEmail registrationEmail,ActivationCode activationCode) {
        setId(DomainRegistry.uniqueIdGeneratorService().id());
        setRegistrationEmail(registrationEmail);
        setActivationCode(activationCode);
    }

    private void setActivationCode(ActivationCode activationCode) {
        this.activationCode = activationCode;
        DomainEventPublisher.instance().publish(new PendingUserActivationCodeUpdated(registrationEmail,activationCode));
    }

    public void newActivationCode(ActivationCode activationCode){
        setActivationCode(activationCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PendingUser)) return false;
        if (!super.equals(o)) return false;
        PendingUser that = (PendingUser) o;
        return Objects.equal(registrationEmail, that.registrationEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), registrationEmail);
    }
}
