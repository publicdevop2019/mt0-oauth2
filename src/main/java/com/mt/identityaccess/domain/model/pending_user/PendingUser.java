package com.mt.identityaccess.domain.model.pending_user;

import com.mt.common.Auditable;
import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@NoArgsConstructor
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
    public void newActivationCode(ActivationCode activationCode){
        setActivationCode(activationCode);
    }
}
